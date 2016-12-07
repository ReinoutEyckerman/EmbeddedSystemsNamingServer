package com.bonkers;

import java.io.File;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by reinout on 12/4/16.
 */
public class FileManager implements QueueListener{
    private final File downloadLocation;
    public QueueEvent<Tuple<String,String>> downloadQueue=new QueueEvent<>();
    private List<String> localFiles;
    private NodeInfo id;
    public FileManager(File downloadLocation, NodeInfo id){
        this.downloadLocation =downloadLocation;
        downloadQueue.addListener(this);
        localFiles=listFilesForFolder(downloadLocation);
        this.id=id;
    }
    public void StartupReplication(ServerIntf server,  NodeInfo prevId){ //TODO: This nodeinfo passing is ugly
        for(int i=0; i<localFiles.size(); i++){
            try {
                String ip = server.findLocationFile(localFiles.get(i));
                if (Objects.equals(id.Address, ip))
                    RequestDownload(prevId.Address, localFiles.get(i));
                else {
                    RequestDownload(ip, localFiles.get(i));
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    public void RecheckOwnership(NodeInfo next){
        for(int i=0; i<localFiles.size(); i++){
            int localHash=HashTableCreator.createHash(localFiles.get(i));
            if(localHash<=id.Hash)
                System.out.println("File will stay");
            else if(localHash<=next.Hash) {
                System.out.println("File will be relocated");
                RequestDownload(next.Address, localFiles.get(i));
            }
            else
                System.out.println("Dere be krakenz here");
        }
    }
    private void RequestDownload(String ip, String file){
        try {
            Registry registry = LocateRegistry.getRegistry(ip);
            NodeIntf node = (NodeIntf) registry.lookup("NodeIntf");
            node.requestDownload(id, file);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void queueFilled() {
        Tuple<String,String> data=downloadQueue.poll();
        new Thread(new TCPClient(data.x,data.y,downloadLocation)).start();//TODO

    }
    /**
     * Returns list of files as strings in a specified folder.
     * @param folderLocation Folder File for where to search for files.
     * @return List of strings of the filenames in the folder.
     */
    private List<String> listFilesForFolder(File folderLocation) {
        List<String> files=new ArrayList<>();
        for (final File fileEntry : folderLocation.listFiles()) {
            if (!fileEntry.isDirectory()) {
                files.add(fileEntry.getName());
            }
        }
        return files;
    }

    public List<String> CheckIfOwner(NodeInfo currNode, NodeInfo prevNode, NodeInfo nextNode)
    {
        List<String> OwnerOfList = new ArrayList<>();
        List<String> fileList = listFilesForFolder(downloadLocation);
        fileList.listIterator().forEachRemaining((file)->{
            int fileHash = HashTableCreator.createHash(file);
            if(fileHash > currNode.Hash)
            {
                if(fileHash < nextNode.Hash)
                {
                    OwnerOfList.add(file);
                }
            }
        });
        return OwnerOfList;
    }
}
