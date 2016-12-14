package com.bonkers;

import java.io.File;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * It is he, FileManager, Manager of Files, Replicator of objects!
 */
public class FileManager implements QueueListener, FileManagerIntf{
    /**
     * The Downloadqueue for downloading files
     */
    public QueueEvent<Tuple<String,String>> downloadQueue;
    /**
     * The location to download to
     */
    private final File downloadLocation;
    /**
     * A Map containing local files and the nodes who are owner of them
     */
    private Map<String,NodeInfo> localFiles;
    /**
     * A List of the files this node owns
     */
    public List<FileInfo>ownedFiles;
    /**
     * This nodes' ID
     */
    private NodeInfo id;
    /**
     * The file checker, used for checking local file updates
     */
    private FileChecker fileChecker;
    /**
     * Server connection interface
     */
    public ServerIntf server;
    private Timer timer;
    /**
     * The constructor, sets up the basic file list
     * @param downloadLocation The location of the files
     * @param id The id of this node
     */
    public FileManager(File downloadLocation, NodeInfo id){
        this.downloadLocation =downloadLocation;
        this.id=id;
        downloadQueue=new QueueEvent<>();
        downloadQueue.addListener(this);
        fileChecker=new FileChecker(downloadLocation);
        localFiles=fileChecker.checkFiles(id);
        ownedFiles=new ArrayList<>();
        for (Map.Entry<String,NodeInfo> file: localFiles.entrySet()) {
            FileInfo f=new FileInfo();
            f.fileName=file.getKey();
            f.fileOwners=new ArrayList<>();
            f.fileOwners.add(file.getValue());
        }
    }
    public void startFileChecker(NodeInfo prevId){
           timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Yo whadup");
                Map<NodeInfo,String> l=fileChecker.checkFiles(id, localFiles);

                for(String file: l.values()){
                    if(!localFiles.containsKey(file)){
                       Replicate(file,prevId);
                    }
                }
            }
        },0,5000);

    }
    /**
     * First replication when the node starts
     * @param prevId Previous node id
     */
    public void StartupReplication(NodeInfo prevId){
        for(Map.Entry<String,NodeInfo> file:localFiles.entrySet()){
            Replicate(file.getKey(),prevId);
        }
    }

    /**
     * Replicates specified file to either the previd or the location the nameserver says
     * @param filename the name of the file
     * @param prevId the id of the previous node
     */
    private void Replicate(String filename,NodeInfo prevId){
        try {
            String ip = server.findLocationFile(filename);
            if (Objects.equals(id.Address, ip)) {
                if (!Objects.equals(prevId.Address, id.Address))
                    RequestDownload(prevId.Address, filename);
            }
            else {
                RequestDownload(ip, filename);
                for (FileInfo file:ownedFiles) {//Todo this can be optimized
                   if(Objects.equals(file.fileName, filename)){
                        setOwnerFile(file);
                        break;
                   }
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Rechecks ownership of files, this gets run when a nextNeighbor gets added
     * @param next NodeInfo of the next neighbor
     */
    public void RecheckOwnership(NodeInfo next){
        for(Map.Entry<String,NodeInfo> file:localFiles.entrySet()){
            int localHash=HashTableCreator.createHash(file.getKey());
            if(localHash<=id.Hash)
                System.out.println("File will stay");
            else if(localHash<=next.Hash) {
                System.out.println("File will be relocated");
                RequestDownload(next.Address, file.getKey());
                for (FileInfo fileInfo:ownedFiles) {//Todo this can be optimized
                    if (Objects.equals(fileInfo.fileName, file.getKey())) {
                        setOwnerFile(fileInfo);
                        break;
                    }
                }
            }
            else
                System.out.println("Dere be krakenz here");
        }
    }

    /**
     * Sends a download request of a file to another node
     * @param ip Ip address of the node
     * @param file file to download
     */
    private void RequestDownload(String ip, String file){
        try {
            System.out.println(ip);
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
        new Thread(new TCPClient(data.x,data.y,downloadLocation)).start();
    }


    /**
     * Sets the ownership of a file, gets called via RMI
     * @param file
     */
    public void setOwnerFile(FileInfo file) {
        file.fileOwners.add(id);
        ownedFiles.add(file);
    }
    public void removeFromFilelist(String file, NodeInfo nodeID){
        localFiles.forEach((filename, id)->{
            if(Objects.equals(file, filename)&& id==nodeID){
               localFiles.remove(file);//Todo might not work
            }
        });
    }
    /**
     * Removes and transports all files where it is owner of, and notifies removal of those it is not
     * @param prevID The id of the previous node
     */
    public void shutdown(NodeInfo prevID){
        timer.purge();
        try {
            Registry registry = LocateRegistry.getRegistry(prevID.Address);
            NodeIntf node = (NodeIntf) registry.lookup("NodeIntf");
            registry=LocateRegistry.getRegistry(server.nodeNeighbors(prevID)[0].Address);
            NodeIntf nextNode = (NodeIntf) registry.lookup("NodeIntf");
            for (FileInfo file : ownedFiles) {
                file.fileOwners.remove(id);
                if(!file.fileOwners.contains(prevID))
                    nextNode.requestDownload(id, file.fileName);
                else
                    node.requestDownload(id, file.fileName);
                node.setOwnerFile(file);
            }
            for(Map.Entry<String, NodeInfo> entry: localFiles.entrySet()){ //Todo can be optimized
               if(!ownedFiles.contains(entry.getKey())){

                }
            }
        } catch (AccessException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }
}
