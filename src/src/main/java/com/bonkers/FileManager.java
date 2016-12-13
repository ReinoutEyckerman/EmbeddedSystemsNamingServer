package com.bonkers;

import java.io.File;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by reinout on 12/4/16.
 */
public class FileManager implements QueueListener{

    public QueueEvent<Tuple<String,String>> downloadQueue;

    private final File downloadLocation;
    private Map<String,NodeInfo> localFiles;
    private List<FileInfo>ownedFiles;
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
        localFiles=fileChecker.checkFiles(id);
        ownedFiles=new ArrayList<>();
        for (Map.Entry<String,NodeInfo> file: localFiles.entrySet()) {
            FileInfo f=new FileInfo();
            f.fileName=file.getKey();
            f.fileOwners=new ArrayList<>();
            f.fileOwners.add(file.getValue());
        }
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Map<NodeInfo,String> l=fileChecker.checkFiles(id, localFiles);
                for(String file: l.values()){
                    if(!localFiles.containsKey(file)){
                       Replicate(file,prevId);
                    }
                }
            }
        }, 0, 30, TimeUnit.SECONDS);
    }

    public void StartupReplication(NodeInfo prevId){
        for(Map.Entry<String,NodeInfo> file:localFiles.entrySet()){
            Replicate(file.getKey(),prevId);

        }
    }
    private void Replicate(String filename,NodeInfo prevId){
        try {
            String ip = server.findLocationFile(filename);
            if (Objects.equals(id.Address, ip))
                RequestDownload(prevId.Address, filename);
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
        new Thread(new TCPClient(data.x,data.y,downloadLocation)).start();
    }

    public List<String> CheckIfOwner(NodeInfo currNode, NodeInfo nextNode)
    {
        List<String> OwnerOfList = new ArrayList<>();
        Map<String,NodeInfo> fileMap = fileChecker.checkFiles(id, localFiles);
        List<String> fileList=new ArrayList(fileMap.keySet());
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
    public void setOwnerFile(FileInfo file) {
        file.fileOwners.add(id);
        ownedFiles.add(file);
    }
}
