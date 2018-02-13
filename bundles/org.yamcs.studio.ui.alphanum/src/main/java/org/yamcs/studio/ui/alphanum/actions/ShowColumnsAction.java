package org.yamcs.studio.ui.alphanum.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.yamcs.studio.ui.alphanum.AlphaNumericEditor;
import org.yamcs.studio.ui.alphanum.ParameterTableViewer;
import org.yamcs.studio.ui.alphanum.ShowColumnsWizard;

public class ShowColumnsAction extends Action implements IEditorActionDelegate {

    private ParameterTableViewer table;

    @Override
    public void run() {
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        ShowColumnsWizard wizard = new ShowColumnsWizard(table.getColumns());
        WizardDialog dialog = new WizardDialog(shell, wizard);
        if (dialog.open() == Window.OK)
            table.setColumns(wizard.getColumns());

    }
    
    @Override
    public void run(IAction action) {
        run();
        
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        ;
        
    }

    @Override
    public void setActiveEditor(IAction action, IEditorPart targetEditor) {
        if(targetEditor == null)
            table = null;
        else
            table = ((AlphaNumericEditor)targetEditor).getParameterTable();

        
    }


}
