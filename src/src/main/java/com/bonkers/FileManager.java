package com.bonkers;

import java.io.File;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

import static com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER;

/**
 * It is he, FileManager, Manager of Files, Replicator of objects!
 */
public class FileManager implements QueueListener {
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
        LOGGER.info("Starting filemanager...");
        new File(System.getProperty("user.dir")+"/tmp").mkdirs();
        this.downloadLocation =downloadLocation;
        this.id=id;
        downloadQueue=new QueueEvent<>();
        downloadQueue.addListener(this);
        fileChecker=new FileChecker(downloadLocation);
        localFiles=fileChecker.checkFiles(id);
        ownedFiles=new ArrayList<>();
        LOGGER.info("Filling ownedFiles with local files for startup.");
        for (Map.Entry<String,NodeInfo> file: localFiles.entrySet()) {
            FileInfo f=new FileInfo();
            f.fileName=file.getKey();
            f.fileOwners=new ArrayList<>();
            f.fileOwners.add(file.getValue());
            ownedFiles.add(f);
            LOGGER.info("Added "+f);
        }
        LOGGER.info("Filemanager successfully started.");
    }

    public void startFileChecker(NodeInfo prevId){
        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //LOGGER.info("Yo whadup");
                Map<String, NodeInfo> l=fileChecker.checkFiles(id, localFiles);
                for(String file: l.keySet()){
                    if(!localFiles.containsKey(file)){
                        FileInfo f=new FileInfo();
                        f.fileName=file;
                        f.fileOwners=new ArrayList<>();
                        f.fileOwners.add(id);
                        ownedFiles.add(f);
                        localFiles.put(file,id);
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
            NodeInfo node = server.findLocationFile(filename);
            if (Objects.equals(id.Address, node.Address)) {
                if (!Objects.equals(prevId.Address, id.Address)) {
                    RequestDownload(prevId, filename);
                    LOGGER.info("Sending "+filename+" with hash"+HashTableCreator.createHash(filename)+" to the previous neighbor.");
                }
            }
            else {
                MoveFileAndChangeOwner(node, filename);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    private void MoveFileAndChangeOwner(NodeInfo node, String filename){
        RequestDownload(node, filename);
        for (FileInfo file:ownedFiles) {//Todo this can be optimized
            if(Objects.equals(file.fileName, filename)){
                try {
                    Registry registry = LocateRegistry.getRegistry(node.Address);
                    NodeIntf nodeIntf = (NodeIntf) registry.lookup("NodeIntf");
                    nodeIntf.setOwnerFile(file);
                    localFiles.replace(filename,node);
                    LOGGER.info("Set "+node+" as new file owner of file "+filename);
                } catch (AccessException e) {
                    e.printStackTrace();
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (NotBoundException e) {
                    e.printStackTrace();
                }
                break;
            }
        }

    }
    /**
     * Rechecks ownership of files, this gets run when a nextNeighbor gets added
     * @param next NodeInfo of the next neighbor
     */
    public void RecheckOwnership(NodeInfo next){
        for(Map.Entry<String,NodeInfo> file:localFiles.entrySet()) {
            try {
                NodeInfo node = server.findLocationFile(file.getKey());
                if(Objects.equals(node.Address, id.Address)){
                    LOGGER.info("File will not be sent to the next neighbor");
                }
                else if(Objects.equals(node.Address,next.Address))
                {
                   LOGGER.info("File will be sent to the next neighbor.");
                    MoveFileAndChangeOwner(next,file.getKey());
                } else
                    System.out.println("Dere be krakenz here");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sends a download request of a file to another node
     * @param file file to download
     */
    private void RequestDownload(NodeInfo nodeInfo, String file){
        try {
            Registry registry = LocateRegistry.getRegistry(nodeInfo.Address);
            LOGGER.info("Sending "+file+" with hash "+HashTableCreator.createHash(file)+" to node "+nodeInfo.Address);
            NodeIntf node = (NodeIntf) registry.lookup("NodeIntf");
            node.requestDownload(id, file);
            for (FileInfo f:ownedFiles) {
                if(Objects.equals(f.fileName,file)&&!f.fileOwners.contains(file)){
                    f.fileOwners.add(nodeInfo);
                    LOGGER.info("Added "+nodeInfo+" as owner of file "+f);
                    break;
                }
            }
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
        ownedFiles.add(file);
        LOGGER.info("Added new file ownership of file "+file);
    }
    public void removeFromFilelist(String file, NodeInfo nodeID){
        localFiles.forEach((filename, id)->{
            if(Objects.equals(file, filename)&& id==nodeID){
                localFiles.remove(file);
                LOGGER.info("Removing "+nodeID+" from file list at file "+ file);
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
                    //Todo ?
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
