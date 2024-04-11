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
@NoArgsConstructor @AllArgsConstructor
public class Page implements Externalizable {
    private String id;
    private String title;
    private List<Component> components;
    private List<Page> subPages;

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(id);
        out.writeUTF(title);
        out.writeInt(components.size());
        for (Component c : components) {
            out.writeObject(c);
        }
        out.writeInt(subPages.size());
        for (Page p : subPages) {
            out.writeObject(p);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        components = new ArrayList<>();
        subPages = new ArrayList<>();
        id = in.readUTF();
        title = in.readUTF();
        int compSize = in.readInt();
        for (int i = 0; i < compSize; i++) {
            components.add((Component) in.readObject());
        }
        int pagesSize = in.readInt();
        for (int i = 0; i < pagesSize; i++) {
            subPages.add((Page) in.readObject());
        }
    }
}
