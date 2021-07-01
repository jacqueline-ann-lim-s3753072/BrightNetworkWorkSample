package com.google;

import java.util.LinkedList;
import java.util.List;

/** A class used to represent a Playlist */
class VideoPlaylist {
    private final String title;
    private final List<Video> videos;

    VideoPlaylist(String title) {
        this.title = title;
        this.videos = new LinkedList<>();
    }

  /** Returns the title of the videoPlaylist. */
  String getTitle() {
    return title;
  }

  /** Returns the list of videos of the videoPlaylist */
  List<Video> getVideos(){
    return videos;
  }
}
