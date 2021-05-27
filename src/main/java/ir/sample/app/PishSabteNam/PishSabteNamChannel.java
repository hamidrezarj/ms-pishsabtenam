package ir.sample.app.PishSabteNam;

import ir.appsan.sdk.APSChannel;
import ir.sample.app.PishSabteNam.services.PishSabteNamService;

public class PishSabteNamChannel extends APSChannel {

    public String getChannelName() {
        return "miniApp";
    }

    public void init() {
        registerService(new PishSabteNamService(getChannelName()));
    }
}
