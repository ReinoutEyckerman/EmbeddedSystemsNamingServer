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
public class FileManager implements QueueListener
{
    /**
     * The location to download to
     */
    private final File downloadLocation;
    /**
     * The Downloadqueue for downloading files
     */
    public QueueEvent<Tuple<String, String>> downloadQueue;
    /**
     * A Map containing local files and the nodes who are owner of them
     */
    public List<String> localFiles;
    /**
     * A List of the files this node owns
     */
    public List<FileInfo> ownedFiles;
    /**
     * Server connection interface
     */
    public ServerIntf server;
    /**
     * This nodes' ID
     */
    private NodeInfo id;
    /**
     * The file checker, used for checking local file updates
     */
    private FileChecker fileChecker;
    /**
     * Timer used for checking for new files every x seconds
     */
    private Timer timer;

    /**
     * The constructor, sets up the basic file list
     *
     * @param downloadLocation The location of the files
     * @param id               The id of this node
     */
    public FileManager(File downloadLocation, NodeInfo id)
    {
        LOGGER.info("Starting filemanager...");
        new File(System.getProperty("user.dir") + "/tmp").mkdirs();
        this.downloadLocation = downloadLocation;
        this.id = id;
        downloadQueue = new QueueEvent<>();
        downloadQueue.addListener(this);
        fileChecker = new FileChecker(downloadLocation);
        localFiles = fileChecker.checkFiles();
        ownedFiles = new ArrayList<>();
        LOGGER.info("Filling ownedFiles with local files for startup.");
        for (String file : localFiles)
        {
            FileInfo f = new FileInfo();
            f.fileName = file;
            f.fileOwners = new ArrayList<>();
            f.fileOwners.add(id);
            ownedFiles.add(f);
            LOGGER.info("Added " + f);
        }
        LOGGER.info("Filemanager successfully started.");
    }

    /**
     * Start timer that checks for new local files every x seconds, and replicates if necessary
     */
    public void startFileChecker()
    {
        timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                List<String> l = fileChecker.checkFiles(localFiles);
                for (String file : l)
                {
                    boolean skip = false;
                    for (FileInfo fileInfo : ownedFiles)
                    {
                        if (Objects.equals(fileInfo.fileName, file))
                        {
                            LOGGER.info("File received, am owner, nothing will happen.");
                            if (!localFiles.contains(file))
                            {
                                localFiles.add(file);
                            }
                            skip = true;
                        }
                    }

                    if (!localFiles.contains(file) && !skip)
                    {
                        FileInfo f = new FileInfo();
                        f.fileName = file;
                        f.fileOwners = new ArrayList<>();
                        f.fileOwners.add(id);
                        ownedFiles.add(f);
                        localFiles.add(file);
                        replicate(file, Client.previd);
                    }
                }
            }
        }, 0, 5000);
    }

    /**
     * First replication when the node starts
     *
     * @param prevId Previous node id
     */
    public void startupReplication(NodeInfo prevId)
    {
        for (String file : localFiles)
        {
            replicate(file, prevId);
        }
    }

    /**
     * Replicates specified file to either the previd or the location the nameserver says
     *
     * @param filename the name of the file
     * @param prevId   the id of the previous node
     */
    private void replicate(String filename, NodeInfo prevId)
    {
        try
        {
            NodeInfo node = server.findLocationFile(filename);
            if (Objects.equals(id.address, node.address))
            {
                if (!Objects.equals(prevId.address, id.address))
                {
                    requestDownload(prevId, filename);
                    LOGGER.info("Sending " + filename + " with hash" + HashTableCreator.createHash(filename) + " to the previous neighbor.");
                }
            }
            else
            {
                moveFileAndChangeOwner(node, filename);
            }
        } catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Replicate given file to given node and make this node owner of this file
     * @param node The target node to receive the file
     * @param filename The name of the file to be transferred
     */
    private void moveFileAndChangeOwner(NodeInfo node, String filename)
    {
        for (FileInfo file : ownedFiles)
        {
            if (Objects.equals(file.fileName, filename))
            {
                try
                {
                    Registry registry = LocateRegistry.getRegistry(node.address);
                    NodeIntf nodeIntf = (NodeIntf) registry.lookup("NodeIntf");
                    file.fileOwners.add(node);
                    nodeIntf.setOwnerFile(file);
                    ownedFiles.remove(file);
                    LOGGER.info("Set " + node + " as new file owner of file " + filename);
                } catch (AccessException e)
                {
                    e.printStackTrace();
                } catch (RemoteException e)
                {
                    e.printStackTrace();
                } catch (NotBoundException e)
                {
                    e.printStackTrace();
                }
                break;
            }
        }
        requestDownload(node, filename);
    }

    /**
     * Rechecks ownership of files, this gets run when a nextNeighbor gets added.
     *
     * @param next NodeInfo of the next neighbor
     */
    public void recheckOwnership(NodeInfo next)
    {
        for (String file : localFiles)
        {
            try
            {
                NodeInfo node = server.findLocationFile(file);
                if (Objects.equals(node.address, id.address))
                {
                    LOGGER.info("File will not be sent to the next neighbor");
                }
                else if (Objects.equals(node.address, next.address))
                {
                    LOGGER.info("File will be sent to the next neighbor.");
                    moveFileAndChangeOwner(next, file);
                }
                else
                {
                    System.out.println("There is a problem if you see this error message. The node this file should be at is not this nor the previous node. Check the following data: File: "+file+" and Node: "+node.toString());
                }
            } catch (RemoteException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sends a download request of a file to another node
     *
     * @param file file to download
     */
    private void requestDownload(NodeInfo nodeInfo, String file)
    {
        try
        {
            Registry registry = LocateRegistry.getRegistry(nodeInfo.address);
            LOGGER.info("Sending " + file + " with hash " + HashTableCreator.createHash(file) + " to node " + nodeInfo.address);
            NodeIntf node = (NodeIntf) registry.lookup("NodeIntf");
            node.requestDownload(id, file);
            for (FileInfo f : ownedFiles)
            {
                if (Objects.equals(f.fileName, file) && !f.fileOwners.contains(nodeInfo))
                {
                    f.fileOwners.add(nodeInfo);
                    LOGGER.info("Added " + nodeInfo + " as owner of file " + f);
                    break;
                }
            }
        } catch (RemoteException e)
        {
            e.printStackTrace();
        } catch (NotBoundException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void queueFilled()
    {
        Tuple<String, String> data = downloadQueue.poll();
        new Thread(new TCPClient(data.x, data.y, downloadLocation)).start();
    }


    /**
     * Sets the ownership of a file, gets called via RMI
     *
     * @param file The file to set ownership of
     */
    public void setOwnerFile(FileInfo file)
    {
        ownedFiles.add(file);
        LOGGER.info("Added new file ownership of file " + file);
    }

    /**
     * Removes given node as download location of the target file
     * @param file the file it does not have anymore
     * @param nodeID the node who doesn't have the file
     */
    public void removeFromOwnerList(String file, NodeInfo nodeID)
    {
        ownedFiles.forEach((fileInfo) ->
        {
            if (Objects.equals(file, fileInfo.fileName))
            {
                ownedFiles.remove(fileInfo);
                LOGGER.info("Removing " + nodeID + " from file list at file " + file);
            }
        });
    }

    /**
     * A getter for localfiles
     * @return list of local files
     */
    public List<String> GetLocalFiles()
    {
        return localFiles;
    }

    /**
     * Removes and transports all files where it is owner of, and notifies removal of those it is not
     *
     * @param prevID The id of the previous node
     */
    public void shutdown(NodeInfo prevID)
    {

        timer.purge();
        try
        {
            Registry registry = LocateRegistry.getRegistry(prevID.address);
            NodeIntf node = (NodeIntf) registry.lookup("NodeIntf");
            registry = LocateRegistry.getRegistry(server.nodeNeighbors(prevID)[0].address);
            NodeIntf nextNode = (NodeIntf) registry.lookup("NodeIntf");
            for (FileInfo file : ownedFiles)
            {
                file.fileOwners.remove(id);
                if (!file.fileOwners.contains(prevID))
                {
                    nextNode.requestDownload(id, file.fileName);
                }
                else
                {
                    node.requestDownload(id, file.fileName);
                }
                node.setOwnerFile(file);
            }
            for (String entry : localFiles)
            {
                if (!ownedFiles.contains(entry))
                {
                    registry = LocateRegistry.getRegistry(server.findLocationFile(entry).address);
                    node = (NodeIntf) registry.lookup("NodeIntf");
                    node.removeFromOwnerList( entry,id);

                }
            }
        } catch (AccessException e)
        {
            e.printStackTrace();
        } catch (RemoteException e)
        {
            e.printStackTrace();
        } catch (NotBoundException e)
        {
            e.printStackTrace();
        }
    }
}
