package org.ethereum.datasource.mapdb;

import org.ethereum.config.SystemProperties;
import org.ethereum.datasource.KeyValueDataSource;
import org.ethereum.db.ByteArrayWrapper;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.lang.System.getProperty;
import static org.ethereum.util.ByteUtil.wrap;

public class MapDBDataSource implements KeyValueDataSource {
    
    private DB db;
    private HTreeMap<ByteArrayWrapper, byte[]> map;
    private String name;

    @Override
    public void init() {
        File dbLocation = new File(getProperty("user.dir") + "/" + SystemProperties.CONFIG.databaseDir() + "/");
        if (!dbLocation.exists()) {
            dbLocation.mkdirs();
        }

        db = DBMaker.newFileDB(new File(dbLocation, name))
                .asyncWriteEnable()
                .mmapFileEnableIfSupported()
                .compressionEnable()
                .cacheSize(512)
                .closeOnJvmShutdown()
                .make();

        this.map = db.createHashMap(name).makeOrGet();
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public byte[] get(byte[] key) {
        return map.get(wrap(key));
    }

    @Override
    public byte[] put(byte[] key, byte[] value) {
        try {
            return map.put(wrap(key), value);
        } finally {
            db.commit();
        }
    }

    @Override
    public void delete(byte[] key) {
        try {
            map.remove(key);
        } finally {
            db.commit();
        }
    }

    @Override
    public Set<byte[]> keys() {
        HashSet<byte[]> result = new HashSet<>();
        for (ByteArrayWrapper key : map.keySet()) {
            result.add(key.getData());    
        }
        return result;
    }

    @Override
    public void updateBatch(Map<byte[], byte[]> rows) {
        try {
            for (byte[] key : rows.keySet()) {
                map.put(wrap(key), rows.get(key));
            }
        } finally {
            db.commit();
        }
    }

    @Override
    public void close() {
        db.close();
    }
}
