package org.csstudio.utility.pvmanager.yamcs.vtype;

import org.epics.vtype.VLong;
import org.yamcs.protobuf.ParameterValue;

public class Uint64VType extends YamcsVType implements VLong {

    public Uint64VType(ParameterValue pval) {
        super(pval);
    }

    @Override
    public Long getValue() {
        return pval.getEngValue().getUint64Value();
    }
    
    @Override
    public String toString() {
        return String.valueOf(pval.getEngValue().getUint64Value());
    }
}
