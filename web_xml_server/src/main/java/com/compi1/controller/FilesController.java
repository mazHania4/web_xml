package com.compi1.controller;

import com.compi1.model.sites.Page;
import com.compi1.model.sites.Site;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.*;

@Builder @AllArgsConstructor
public class FilesController implements Externalizable {

    @Getter private List<String> siteIds;
    private Map<String, String> page_siteIds;
    private Map<String, List<String>> pages_tags;
    public static final String rootFolder = "/home/hania/Desktop/web_xml_sites/";

    public void addPage(Page page, String siteId) throws RuntimeException {
        try {
            writeSerialized(page, siteId+"/"+page.getId());
        } catch (IOException e) {
            throw new RuntimeException("Couldn't externalize page '"+page.getId()+"' "+e.getMessage());
        }
        //update site's map of visits
        Site site = getSite(siteId);
        site.getPage_visits().put(page.getId(), 0);
        rewriteSite(site);
        //update parent_page's list of subpages
        Page parent = getPage(page.getParentId());
        parent.getSubPageIds().add(page.getId());
        page_siteIds.put(page.getId(), siteId);
        pages_tags.put(page.getId(), page.getTags());
        rewritePage(parent);
        saveIds();
    }

    public void addSite(Site site, Page index) throws RuntimeException {
        try {
            new File(rootFolder+site.getId()).mkdirs();
            writeSerialized(site, site.getId()+"/site");
            writeSerialized(index, site.getId()+"/"+index.getId());
        } catch (IOException e) {
            throw new RuntimeException("Couldn't create folder and externalize site '"+site.getId()+"'");
        }
        page_siteIds.put(index.getId(), site.getId());
        siteIds.add(site.getId());
        saveIds();
    }

    public void addVisit(String siteId, String pageId){
        Site site = getSite(siteId);
        site.setVisits(site.getVisits()+1);
        Map<String, Integer> map = site.getPage_visits();
        map.replace(pageId, map.get(pageId)+1);
        rewriteSite(site);
    }

    public int getVisits(String siteId, String pageId){
        Map<String, Integer> map = getSite(siteId).getPage_visits();
        return map.get(pageId);
    }
    public Map<String, Integer> getPopularPages(String siteId){
        Map<String, Integer> map = getSite(siteId).getPage_visits();
        Map<String, Integer> pages = new HashMap<>();
        PriorityQueue<Map.Entry<String, Integer>> pq = new PriorityQueue<>((a, b) -> b.getValue()-a.getValue());
        for (Map.Entry<String, Integer> entry : map.entrySet()){
            pq.offer(entry);
            if (pq.size() > 10) pq.poll();
        }
        while (!pq.isEmpty()){
            Map.Entry<String, Integer> entry = pq.poll();
            pages.put(entry.getKey(), entry.getValue());
        }
        return pages;
    }

    public int getVisits(String siteId){
        return getSite(siteId).getVisits();
    }

    public void deletePage(String id) throws RuntimeException {
        Page page = getPage(id);
        List<String> pages = page.getSubPageIds();
        pages.add(id);
        for (String p:pages) {
            File file = new File(rootFolder + page_siteIds.get(p) + "/" + p + ".ser");
            if (!file.delete()) throw new RuntimeException("Failed to delete the page '"+p+"'");
            page_siteIds.remove(p);
            pages_tags.remove(p);
        }
        //update parent_page's list of subpages
        Page parent = getPage(page.getParentId());
        parent.getSubPageIds().remove(id);
        rewritePage(parent);
        saveIds();
    }

    public void deleteSite(String id) throws RuntimeException {
        try {
            FileUtils.deleteDirectory(new File(rootFolder + id));
            siteIds.remove(id);
            List<String> pages = new ArrayList<>();
            page_siteIds.forEach((key, value) -> { if(value.equals(id)) pages.add(key); } );
            page_siteIds.entrySet().removeIf(entry -> id.equals(entry.getValue()));
            pages.forEach(p -> pages_tags.remove(p));
        } catch (IOException | IllegalArgumentException e) {
            throw new RuntimeException("Couldn't delete site '"+id+"'");
        }
        saveIds();
    }

    public void rewritePage(Page page) throws RuntimeException {
        try {
            String siteId = page_siteIds.get(page.getId());
            pages_tags.put(page.getId(), page.getTags());
            writeSerialized(page, siteId+"/"+page.getId());
        } catch (IOException e) {
            throw new RuntimeException("Couldn't re-externalize page '"+page.getId()+"'");
        }
    }

    private void rewriteSite(Site site) throws RuntimeException {
        try {
            writeSerialized(site, site.getId()+"/site");
        } catch (IOException e) {
            throw new RuntimeException("Couldn't re-externalize site '"+site.getId()+"'");
        }
    }

    public Site getSite(String id) throws RuntimeException {
        try {
            return (Site) readSerialized(id+"/site");
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Couldn't retrieve site '"+id+"'"+e.getMessage());
        }
    }

    public Page getPage(String id) throws RuntimeException {
        String siteId = page_siteIds.get(id);
        try {
            return (Page) readSerialized(siteId+"/"+id);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Couldn't retrieve page '"+id+"'");
        }
    }

    public List<String> getPageIdsWithTag(String tag) {
        //falta verificar que sean del mismo sitio dado
        List<String> pages = new ArrayList<>();
        pages_tags.forEach((key, value) -> { if(value.contains(tag)) pages.add(key); } );
        return pages;
    }

    private void getSubPagesR(String parent, List<String> list) {
        for (String page: getPage(parent).getSubPageIds()) {
            list.add(page);
            getSubPagesR(page, list);
        }
    }
    public List<String> getSubPages(String parent){
        List<String> list = new ArrayList<>();
        getSubPagesR(parent, list);
        return list;
    }

    private Object readSerialized(String nameFromRF) throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(rootFolder + nameFromRF + ".ser");
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        Object obj = objectInputStream.readObject();
        objectInputStream.close();
        return obj;
    }
    private void writeSerialized(Object obj, String nameFromRF) throws IOException {
        FileOutputStream fileOutputStream;
        fileOutputStream = new FileOutputStream(rootFolder + nameFromRF + ".ser");
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(obj);
        objectOutputStream.close();
    }

    public boolean siteIdExists(String id){
        return siteIds.contains(id);
    }
    public boolean pageIdExists(String id) {
        return page_siteIds.containsKey(id);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(siteIds.size());
        for (String i : siteIds) {
            out.writeUTF(i);
        }
        out.writeObject(page_siteIds);
        out.writeObject(pages_tags);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        //siteIds = new ArrayList<>();
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            siteIds.add(in.readUTF());
        }
        //pageIds = new ArrayList<>();
        page_siteIds = (Map<String, String>) in.readObject();
        pages_tags = (Map<String, List<String>>) in.readObject();
    }

    private void saveIds(){
        try {
            writeSerialized(this, "ids");
        } catch (IOException e) {
            System.out.println("(!) Couldn't save serialized id lists");
            e.printStackTrace();
        }
    }

    public FilesController() {
        siteIds = new ArrayList<>();
        page_siteIds = new HashMap<>();
        pages_tags = new HashMap<>();
    }
}
