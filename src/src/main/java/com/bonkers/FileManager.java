package com.bonkers;

import java.io.File;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by reinout on 12/4/16.
 */
public class FileManager implements QueueListener{
    private final File downloadLocation;
    public QueueEvent<Tuple<String,String>> downloadQueue;
    private List<String> localFiles;
    private NodeInfo id;
    private FileChecker fileChecker;
    private ServerIntf server;
    public FileManager(File downloadLocation, ServerIntf server, NodeInfo id, NodeInfo prevId){
        this.downloadLocation =downloadLocation;
        this.id=id;
        this.server=server;
        downloadQueue=new QueueEvent<>();
        downloadQueue.addListener(this);
        fileChecker=new FileChecker(downloadLocation);
        localFiles=fileChecker.checkFiles();
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                List<String> l=fileChecker.checkFiles(localFiles);
                for(String file: l){
                    if(!localFiles.contains(file)){
                       Replicate(file,prevId);
                    }
                }
            }
        }, 0, 30, TimeUnit.SECONDS);
    }
    public void StartupReplication(  NodeInfo prevId){ //TODO: This nodeinfo passing is ugly
        for(int i=0; i<localFiles.size(); i++){
            Replicate(localFiles.get(i),prevId);
        }
    }
    private void Replicate(String filename,NodeInfo prevId){
        try {
            String ip = server.findLocationFile(filename);
            if (Objects.equals(id.Address, ip))
                RequestDownload(prevId.Address, filename);
            else {
                RequestDownload(ip, filename);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
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

    public List<String> CheckIfOwner(NodeInfo currNode, NodeInfo nextNode)
    {
        List<String> OwnerOfList = new ArrayList<>();
        List<String> fileList = fileChecker.checkFiles(localFiles);
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
