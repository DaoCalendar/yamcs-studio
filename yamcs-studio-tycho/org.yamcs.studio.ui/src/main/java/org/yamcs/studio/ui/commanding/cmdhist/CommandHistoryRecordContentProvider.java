package org.yamcs.studio.ui.commanding.cmdhist;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.yamcs.protobuf.Commanding.CommandHistoryAttribute;
import org.yamcs.protobuf.Commanding.CommandHistoryEntry;
import org.yamcs.protobuf.Commanding.CommandId;
import org.yamcs.studio.ui.commanding.PTVInfo;

public class CommandHistoryRecordContentProvider implements IStructuredContentProvider {

    public static final String GREEN = "icons/obj16/ok.png";
    public static final String RED = "icons/obj16/nok.png";

    private static final String VERIFIER_COMPLETE = "CommandComplete";
    private static final String VERIFIER_PREFIX = "Verifier_";
    private static final String ACKNOWLEDGE_PREFIX = "Acknowledge_";
    private static final String STATUS_SUFFIX = "_Status";
    private static final String TIME_SUFFIX = "_Time";

    public static final String ATTR_TRANSMISSION_CONSTRAINTS = "TransmissionConstraints";
    public static final String ATTR_COMMAND_FAILED = "CommandFailed";
    public static final String ATTR_FINAL_SEQUENCE_COUNT = "Final_Sequence_Count";
    public static final String ATTR_SOURCE = "source";
    public static final String ATTR_USERNAME = "username";

    private Map<CommandId, CommandHistoryRecord> recordsByCommandId = new LinkedHashMap<>();
    private TableViewer tableViewer;
    private boolean scrollLock;

    public CommandHistoryRecordContentProvider(TableViewer tableViewer) {
        this.tableViewer = tableViewer;
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        // TODO could happen when switching channels
    }

    @Override
    public void dispose() {
    }

    @Override
    public Object[] getElements(Object inputElement) {
        return recordsByCommandId.values().toArray();
    }

    public void processCommandHistoryEntry(CommandHistoryEntry entry) {
        CommandId commandId = entry.getCommandId();
        CommandHistoryRecord rec;
        boolean create;
        if (recordsByCommandId.containsKey(commandId)) {
            rec = recordsByCommandId.get(commandId);
            create = false;
        } else {
            rec = new CommandHistoryRecord(commandId);
            recordsByCommandId.put(commandId, rec);
            create = true;
        }

        // Autoprocess attributes for additional columns
        for (CommandHistoryAttribute attr : entry.getAttrList()) {
            String shortName = attr.getName()
                    //  .replace(VERIFIER_PREFIX, "")
                    .replace(ACKNOWLEDGE_PREFIX, "")
                    .replace(STATUS_SUFFIX, "")
                    .replace(TIME_SUFFIX, "");
            if (attr.getName().endsWith(STATUS_SUFFIX)
                    || attr.getName().startsWith(VERIFIER_PREFIX)
                    || attr.getName().equals(VERIFIER_COMPLETE)) {
                if (attr.getValue().getStringValue().contains("OK"))
                    rec.addCellImage(shortName, GREEN);
                else
                    rec.addCellImage(shortName, RED);
            } else if (attr.getName().equals(ATTR_FINAL_SEQUENCE_COUNT)) {
                rec.setFinalSequenceCount(attr.getValue());
            } else if (attr.getName().equals(ATTR_SOURCE)) {
                rec.setSource(attr.getValue());
            } else if (attr.getName().equals(ATTR_USERNAME)) {
                rec.setUsername(attr.getValue());
            } else if (attr.getName().equals(ATTR_TRANSMISSION_CONSTRAINTS)) {
                rec.getPTVInfo().setState(PTVInfo.State.fromYamcsValue(attr.getValue()));
            } else if (attr.getName().equals(ATTR_COMMAND_FAILED)) {
                rec.getPTVInfo().setFailureMessage(attr.getValue().getStringValue());
            } else {
                rec.addCellValue(shortName, attr.getValue());
            }
        }

        // All done, make changes visible
        if (create) {
            tableViewer.add(rec);
            maybeSelectAndReveal(rec);
        } else {
            tableViewer.update(rec, null); // Null, means all properties
            maybeSelectAndReveal(rec);

        }
    }

    public void maybeSelectAndReveal(CommandHistoryRecord rec) {
        if (!scrollLock) {
            IStructuredSelection sel = new StructuredSelection(rec);
            tableViewer.setSelection(sel, true);
        }
    }

    public void enableScrollLock(boolean enabled) {
        scrollLock = enabled;
    }
}
