package org.dbflute.erflute.preference;

import java.io.File;
import java.util.StringTokenizer;

import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class MultiFileFieldEditor extends FileFieldEditor {

    private String[] extensions = null;
    private boolean multiple = false;

    public MultiFileFieldEditor(String name, String labelText, Composite parent) {
        super(name, labelText, false, parent);
    }

    @Override
    protected String changePressed() {
        final StringTokenizer tokenizer = new StringTokenizer(getTextControl().getText(), ";");
        File f;
        if (tokenizer.countTokens() == 0) {
            f = null;
        } else {
            f = new File(tokenizer.nextToken());
            if (!f.exists()) {
                f = null;
            }
        }
        final File[] d = getFile(f);
        if (d == null || d.length == 0) {
            return null;
        }
        final StringBuilder ret = new StringBuilder();
        for (int i = 0; i < d.length; i++) {
            ret.append(d[i].getAbsolutePath());
            ret.append(";");
        }
        return ret.toString();
    }

    private File[] getFile(File startingDirectory) {
        int style = SWT.OPEN;
        if (multiple) {
            style |= SWT.MULTI;
        }
        final FileDialog dialog = new FileDialog(getShell(), style);
        if (startingDirectory != null) {
            dialog.setFileName(startingDirectory.getPath());
        }
        if (extensions != null) {
            dialog.setFilterExtensions(extensions);
        }
        dialog.open();
        final String[] fileNames = dialog.getFileNames();
        if (fileNames.length > 0) {
            final File[] files = new File[fileNames.length];
            for (int i = 0; i < fileNames.length; i++) {
                files[i] = new File(dialog.getFilterPath(), fileNames[i]);
            }
            return files;
        }
        return null;
    }

    @Override
    public void setFileExtensions(String[] extensions) {
        this.extensions = extensions;
    }

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }
}
