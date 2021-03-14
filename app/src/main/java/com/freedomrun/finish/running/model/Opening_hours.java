package com.freedomrun.finish.running.model;

import org.jetbrains.annotations.NotNull;

public class Opening_hours {
    private String open_now;

    public String getOpen_now ()
    {
        return open_now;
    }

    public void setOpen_now (String open_now)
    {
        this.open_now = open_now;
    }

    @NotNull
    @Override
    public String toString()
    {
        return "ClassPojo [open_now = "+open_now+"]";
    }
}
