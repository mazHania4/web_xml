package com.compi1.model.sites;


import lombok.*;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.LocalDate;
import java.util.Map;

@Getter @Setter
@Builder @ToString
@NoArgsConstructor
@AllArgsConstructor
public class Site implements Externalizable {
    private String id;
    private String cr_user;
    private LocalDate cr_date;
    private String mod_user;
    private LocalDate mod_date;
    private Map<String, Integer> page_visits;

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(id);
        out.writeUTF(cr_user);
        out.writeObject(cr_date);
        out.writeUTF(mod_user);
        out.writeObject(mod_date);
        out.writeObject(page_visits);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        id = in.readUTF();
        cr_user = in.readUTF();
        cr_date = (LocalDate) in.readObject();
        mod_user = in.readUTF();
        mod_date = (LocalDate) in.readObject();
        page_visits = (Map<String, Integer>) in.readObject();
    }
}
