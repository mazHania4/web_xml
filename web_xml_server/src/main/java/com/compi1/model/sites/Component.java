package com.compi1.model.sites;

import lombok.*;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@Builder @ToString
@NoArgsConstructor
@AllArgsConstructor
public class Component implements Externalizable {
    private String id;
    private ComponentType type;
    private String text;
    private Alignment alignment;
    private String color;
    private String src;
    private int height;
    private int width;
    private String parent;
    private List<String> tags;

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(id);
        out.writeObject(type);
        out.writeUTF(text);
        out.writeObject(alignment);
        out.writeUTF(color);
        out.writeUTF(src);
        out.writeInt(height);
        out.writeInt(width);
        out.writeUTF(parent);
        out.writeInt(tags.size());
        for (String t : tags) {
            out.writeUTF(t);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        tags = new ArrayList<>();
        id = in.readUTF();
        type = (ComponentType) in.readObject();
        text = in.readUTF();
        alignment = (Alignment) in.readObject();
        color = in.readUTF();
        src = in.readUTF();
        height = in.readInt();
        width = in.readInt();
        parent = in.readUTF();
        int tagSize = in.readInt();
        for (int i = 0; i < tagSize; i++) {
            tags.add(in.readUTF());
        }
    }
}
