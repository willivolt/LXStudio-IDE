package org.projectempire.lx.osc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import heronarts.lx.LX;
import heronarts.lx.osc.LXOscEngine;
import heronarts.lx.osc.LXOscListener;
import heronarts.lx.osc.OscMessage;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class OscCueListener implements LXOscListener {
    private final LX lx;
    private LXOscEngine.Transmitter transmitter;
    private AtomicInteger cue = new AtomicInteger(0);
    Map<String, List<EmpireOscCue>> cueMap = Collections.emptyMap();

    public OscCueListener(LX lx) {
        this.lx = lx;
        loadCueJson();
        try {
            transmitter = lx.engine.osc.transmitter("127.0.0.1", lx.engine.osc.receivePort.getValuei());
        } catch (Exception e) {
            LX.error(e);
        }
    }

    @Override
    public void oscMessage(OscMessage message) {
        LX.log("Cue: " + cue.incrementAndGet() + " " + message.toString());
        String address = message.getAddressPattern().toString();
        if (address != address.trim()) {
            LX.error("Address has leading or trailing whitespace: \"" + address + "\"");
        }
        String[] parts = address.split("/");
        if (parts.length > 0) {
            if ("empire".equals(parts[1])) {
                if (parts.length > 2 && "cue".equals(parts[2])) {
                    if (message.size() > 0) {
                        String cueName = message.get(0).toString();
                        List<EmpireOscCue> cues = getCue(cueName);
                        if (cues != null) {
                            runCues(cues);
                        } else {
                            LX.error("Cue not found: " + cueName);
                        }
                    } else {
                        LX.error("Cue name not specified");
                    }
                } else {
                    LX.error("Unknown empire command: " + address);
                }
            } else if ("lx".equals(parts[1])) {
                // pass message to lx engine
               lx.engine.handleOscMessage(message, parts, 2);
            }
        }
    }

    private List<EmpireOscCue> getCue(String name) {
        return cueMap.get(name);
    }

    private void runCues(List<EmpireOscCue> cues) {
        for (EmpireOscCue cue : cues) {
            OscMessage message = cue.toOscMessage();
            if (message.hasPrefix("/lx")) {
                try {
                    transmitter.send(message);
                } catch (IOException e) {
                    LX.error(e);
                }
            }
        }
    }
    private void loadCueJson() {
        File f = lx.getMediaFile(LX.Media.PROJECTS,"cues.json");
        if (!f.exists()) {
            LX.error("Cues file does not exist: " + f.getAbsolutePath());
            return;
        }
        Gson gson = new GsonBuilder().create();
        Map<String, List<EmpireOscCue>> map = Collections.emptyMap();
        final Type typeOf = new TypeToken<Map<String, List<EmpireOscCue>>>(){}.getType();
        try {
            map = gson.fromJson(new FileReader(f), typeOf);
        } catch (Exception e) {
            LX.error(e);
        };
        cueMap = map;
    }

    private Map<String, List<EmpireOscCue>> getMessageMap() {
        Map<String, List<EmpireOscCue>> map = new HashMap<>();
        EmpireOscCue message = new EmpireOscCue();
        message.setAddress("/lx/layer/0/fill");
        message.add(1);
        message.add("2 3 4");
        message.add(5.0);
        map.put("one", List.of(message));
        message = new EmpireOscCue();
        message.setAddress("/lx/layer/2/fill");
        message.add(2);
        message.add("5 3 4");
        message.add(10.0);
        map.put("two", List.of(message));
        return map;
    }

    class EmpireOscCue {
        private String address = "";
        private List<Object> arguments = new ArrayList<>();

        public EmpireOscCue() {
        }

        public EmpireOscCue(OscMessage oscMessage) {
            this.address = oscMessage.getAddressPattern().getValue();
            for (int i = 0; i < oscMessage.size(); i++) {
                arguments.add(oscMessage.get(i).toString());
            }
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public void setArguments(List<Object> arguments) {
            this.arguments = arguments;
        }

        public void add(String argument) {
            arguments.add(argument);
        }

        public void add(int argument) {
            arguments.add(argument);
        }

        public void add(double argument) {
            arguments.add(argument);
        }

        public OscMessage toOscMessage() {
            OscMessage message = new OscMessage(address);
            for (Object argument : arguments) {
                if (argument instanceof String) {
                    message.add((String) argument);
                } else if (argument instanceof Integer) {
                    message.add((Integer) argument);
                } else if (argument instanceof Double) {
                    message.add((Double) argument);
                } else {
                    message.add(String.valueOf(argument));
                    LX.error("Unknown argument type: " + argument.getClass().getName());
                }
//                try {
//                    double d = Double.parseDouble(argument);
//                    message.add(d);
//                } catch (NumberFormatException e) {
//                    message.add(argument);
//                }
            }
            return message;
        }
    }
}
