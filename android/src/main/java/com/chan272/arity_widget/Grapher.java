// Copyright (C) 2009-2010 Mihai Preda

package com.chan272.arity_widget;

import org.javia.arity.Function;

public interface Grapher {
    static final String SCREENSHOT_DIR = "/screenshots";

    public void setFunction(Function f);

    public void onPause();

    public void setDirty(Boolean dirty);

    public void onResume();

    public String captureScreenshot();
}
