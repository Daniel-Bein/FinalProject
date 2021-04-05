package com.example.finalproject.audio;

public class AudioSearch {

        String search;
        boolean isMine;

        public AudioSearch(String search, boolean isMine){
            super();
            this.search = search;
            this.isMine = isMine;
        }
        public String getSearch(){
            return search;

        }
        public void setSearch(String search){
            this.search = search;
        }
        public boolean isMine(){
            return isMine;
        }
        public void setMine(boolean isMine) {
            this.isMine = isMine;
        }



    }


