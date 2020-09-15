package org.yamcs.studio.data.yamcs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.yamcs.protobuf.Mdb.EnumValue;
import org.yamcs.protobuf.Mdb.ParameterTypeInfo;
import org.yamcs.protobuf.Pvalue.ParameterValue;
import org.yamcs.protobuf.Yamcs.NamedObjectId;
import org.yamcs.studio.core.YamcsPlugin;
import org.yamcs.studio.data.vtype.VEnum;

public class EnumeratedVType extends YamcsVType implements VEnum {

    public EnumeratedVType(ParameterValue pval) {
        super(pval);
    }

    @Override
    public int getIndex() {
        return (int) pval.getEngValue().getSint64Value();
    }

    @Override
    public String getValue() {
        return pval.getEngValue().getStringValue();
    }

    @Override
    public List<String> getLabels() {

        // TODO Get an id matching the qualified name from the info object
        // (not e.g. the opsname)
        // But be careful that any suffixes ('[]' or '.') are kept
        NamedObjectId id = NamedObjectId.newBuilder()
                .setName(pval.getId().getName())
                .build();

        ParameterTypeInfo specificPtype = YamcsPlugin.getMissionDatabase().getParameterTypeInfo(id);
        return getLabelsForType(specificPtype);
    }

    static List<String> getLabelsForType(ParameterTypeInfo ptype) {
        if (ptype == null) {
            return Collections.emptyList();
        }
        List<EnumValue> enumValues = ptype.getEnumValueList();
        if (enumValues != null) {
            List<String> labels = new ArrayList<>(enumValues.size());
            for (EnumValue enumValue : enumValues) {
                labels.add(enumValue.getLabel());
            }
            return labels;
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public String toString() {
        // Use String.valueOf, because it formats a nice "null" string
        // in case it is null
        return String.valueOf(pval.getEngValue().getStringValue());
    }
}