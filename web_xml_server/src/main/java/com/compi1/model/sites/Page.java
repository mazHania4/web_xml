package com.compi1.model.sites;

import lombok.*;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@Builder @ToString
@NoArgsConstructor @AllArgsConstructor
public class Page implements Externalizable {
    private String parentId;
    private String id;
    private String title;
    private List<Component> components;
    private List<String> subPageIds;
    private List<String> tags;
    private String cr_user;
    private LocalDate cr_date;
    private String mod_user;
    private LocalDate mod_date;

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(parentId != null ? parentId : "-");
        out.writeUTF(id);
        out.writeUTF(title);
        out.writeInt(components.size());
        for (Component c : components) {
            out.writeObject(c);
        }
        out.writeInt(subPageIds.size());
        for (String p : subPageIds) {
            out.writeUTF(p);
        }
        out.writeInt(tags.size());
        for (String t : tags) {
            out.writeUTF(t);
        }
        out.writeUTF(cr_user);
        out.writeObject(cr_date);
        out.writeUTF(mod_user);
        out.writeObject(mod_date);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        components = new ArrayList<>();
        subPageIds = new ArrayList<>();
        tags = new ArrayList<>();
        parentId = in.readUTF();
        id = in.readUTF();
        title = in.readUTF();
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            components.add((Component) in.readObject());
        }
        size = in.readInt();
        for (int i = 0; i < size; i++) {
            subPageIds.add( in.readUTF());
        }
        size = in.readInt();
        for (int i = 0; i < size; i++) {
            tags.add( in.readUTF());
        }
        cr_user = in.readUTF();
        cr_date = (LocalDate) in.readObject();
        mod_user = in.readUTF();
        mod_date = (LocalDate) in.readObject();
    }
}
