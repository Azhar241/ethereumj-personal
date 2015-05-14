package org.ethereum.vmtrace;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.ethereum.vm.DataWord;
import org.spongycastle.util.encoders.Hex;

import java.nio.ByteBuffer;
import java.util.*;

import static org.ethereum.vmtrace.Serializers.*;

/**
 * Data to for one program step to save.
 *
 *   {
 *    'op': 'CODECOPY'
 *    'storage': {},
 *    'gas': '99376',
 *    'deep': 0,
 *    'pc': '9',
 *    'memory': '',
 *    'stack': ['15', '15', '14', '0'],
 *   }
 *
 * @author Roman Mandeleil
 * @since 28.10.2014
 */
public class Op {

    @JsonSerialize(using = OpCodeSerializer.class)
    private byte op;
    private int deep;
    private int pc;
    @JsonSerialize(using = DataWordSerializer.class)
    private DataWord gas;
    @JsonSerialize(using = ByteArraySerializer.class)
    private byte[] memory;
    private List<String> stack;
    private Map<String, String> storage;

    public void setOp(byte op) {
        this.op = op;
    }

    public void setDeep(int deep) {
        this.deep = deep;
    }

    public void setPc(int pc) {
        this.pc = pc;
    }

    public void saveGas(DataWord gas) {
        this.gas = gas;
    }

    public void saveStorageMap(Map<DataWord, DataWord> storage) {

        this.storage = new HashMap<>();
        List<DataWord> keys = new ArrayList<>(storage.keySet());
        Collections.sort(keys);
        for (DataWord key : keys) {
            DataWord value = storage.get(key);
            this.storage.put(Hex.toHexString(key.getData()),
                    Hex.toHexString(value.getData()));
        }
    }

    public void saveMemory(byte[] memory) {
        this.memory = memory;
    }

    public void saveStack(Stack<DataWord> stack) {

        this.stack = new ArrayList<>();

        for (DataWord element : stack) {
            this.stack.add(0, Hex.toHexString(element.getData()));
        }
    }

    public String toString() {
        return asJsonString();
    }

    private String asJsonString() {
        return serializeFieldsOnly(this, false);
    }
}
