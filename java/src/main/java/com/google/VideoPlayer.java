package com.google;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Pattern;


public class VideoPlayer {

  private final VideoLibrary videoLibrary;
  private LinkedList<VideoPlaylist> videoPlayLists;
  private Video currentVideo;
  private Video pausedVideo;

  public VideoPlayer() {
    this.videoLibrary = new VideoLibrary();
    this.videoPlayLists = new LinkedList<>();
    this.currentVideo = null;
    this.pausedVideo = null;
  }

  public void numberOfVideos() {
    System.out.printf("%s videos in the library%n", videoLibrary.getVideos().size());
  }

  public void showAllVideos() {
    System.out.println("Here's a list of all available videos:");
    List<Video> videosList = videoLibrary.getVideos();
    videosList.sort((v1, v2) -> v1.getTitle().compareTo(v2.getTitle()));
    for(Video v: videosList)
    {
      String flagtext = "";
      if(v.getFlagStatus())
      {
        flagtext = String.format("- FLAGGED (reason: %s)", v.getFlagReason());
      }
      System.out.printf("%s (%s) %s %s\n", v.getTitle(), v.getVideoId(), v.getTags().toString().replace("," , ""), flagtext);
    }
  }

  private boolean checkIfVideoExists(String videoId, List<Video> videos){
    boolean exist = false;
    for(Video v: videos)
    {
      if(videoId.equals(v.getVideoId()))
      {
        exist = true;
      }
    }
    return exist;
  }

  public void playVideo(String videoId) {
    Video selectedVideo = null;
    if(checkIfVideoExists(videoId, videoLibrary.getVideos()))
    {
      selectedVideo = videoLibrary.getVideo(videoId);
      if(selectedVideo.getFlagStatus() == false)
      {
        if(currentVideo != null)
        {
            System.out.printf("Stopping video: %s\n", currentVideo.getTitle());
        }
        System.out.printf("Playing video: %s\n", selectedVideo.getTitle());
        pausedVideo = null;
      }
      else
      {
        System.out.printf("Cannot play video: Video is currently flagged (reason: %s)\n", selectedVideo.getFlagReason());
      }
    }
    else{
      System.out.println("Cannot play video: Video does not exist");
    }
    currentVideo = selectedVideo;
  }

  public void stopVideo() {
    if(currentVideo != null)
    {
      System.out.printf("Stopping video: %s\n", currentVideo.getTitle());
      currentVideo = null;
    }
    else
    {
      System.out.println("Cannot stop video: No video is currently playing");
    }
  }

  private boolean checkIfAllVideosAreFlagged()
  {
    boolean check = true;
    for(Video v: videoLibrary.getVideos())
    {
      if(v.getFlagStatus() == false)
      {
        check = false;
      }
    }
    return check;
  }

  public void playRandomVideo() {
    Video selectedVideo = null;
    if(!(videoLibrary.getVideos().isEmpty()))
    {
      if(checkIfAllVideosAreFlagged() == false)
      {
        while(selectedVideo == null)
        {
          Random rand = new Random();
          Video randomVideo = videoLibrary.getVideos().get(rand.nextInt(videoLibrary.getVideos().size()));
          if(randomVideo.getFlagStatus() == false)
          {
            selectedVideo = randomVideo; 
          }
        }
      }
    }
    if(selectedVideo != null)
    {
      playVideo(selectedVideo.getVideoId());
    }
    else
    {
      System.out.println("No videos available");
    }
  }

  public void pauseVideo() {
    if(currentVideo != null)
    {
      if(currentVideo != pausedVideo)
      {
        System.out.printf("Pausing video: %s\n", currentVideo.getTitle());
        pausedVideo = currentVideo;
      }
      else
      {
        System.out.printf("Video already paused: %s\n", pausedVideo.getTitle());
      }
    }
    else
    {
      System.out.println("Cannot pause video: No video is currently playing");
    }
  }

  public void continueVideo() {
    if(currentVideo != null)
    {
      if(currentVideo == pausedVideo)
      {
        System.out.printf("Continuing video: %s\n", currentVideo.getTitle());
        pausedVideo = null;
      }
      else
      {
        System.out.println("Cannot continue video: Video is not paused");
      }
    }
    else
    {
      System.out.println("Cannot continue video: No video is currently playing");
    }
  }

  public void showPlaying() {
    if(currentVideo != null)
    {
      String pausedtext = "";
      if(currentVideo == pausedVideo)
      {
        pausedtext = "- PAUSED";
      }
      System.out.printf("Currently playing: %s (%s) %s %s\n", currentVideo.getTitle(), currentVideo.getVideoId(), currentVideo.getTags().toString().replace("," , ""), pausedtext);
    }
    else
    {
      System.out.println("No video is currently playing");
    }
  }

  private boolean checkIfPlaylistExists(String playlistName){
    boolean exist = false;
    for(VideoPlaylist vPlaylist: videoPlayLists)
    {
      if(vPlaylist.getTitle().equalsIgnoreCase(playlistName))
      {
        exist = true;
      }
    }
    return exist;
  }

  private VideoPlaylist getVideoPlaylist(String playlistName){
    VideoPlaylist selectedVideoPlaylist = null;
    for(VideoPlaylist vPlaylist: videoPlayLists)
    {
      if(vPlaylist.getTitle().toLowerCase().equals(playlistName.toLowerCase()))
      {
        selectedVideoPlaylist = vPlaylist;
      }
    }
    return selectedVideoPlaylist;
  }

  public void createPlaylist(String playlistName) {
    if(checkIfPlaylistExists(playlistName) == false)
    {
      System.out.printf("Successfully created new playlist: %s\n", playlistName);
      //adds to the front of the linkedlist
      videoPlayLists.push(new VideoPlaylist(playlistName));
    }
    else
    {
      System.out.println("Cannot create playlist: A playlist with the same name already exists");
    }
  }

  public void addVideoToPlaylist(String playlistName, String videoId) {
    if(checkIfPlaylistExists(playlistName))
    {
      VideoPlaylist selectedVideoPlaylist = getVideoPlaylist(playlistName);
      if(checkIfVideoExists(videoId, videoLibrary.getVideos()))
      {
        Video selectedVideo = videoLibrary.getVideo(videoId);
        if(selectedVideo.getFlagStatus() == false)
        {
          if(!selectedVideoPlaylist.getVideos().contains(selectedVideo))
          {
            selectedVideoPlaylist.getVideos().add(selectedVideo);
            System.out.printf("Added video to %s: %s\n", playlistName, selectedVideo.getTitle());
          }
          else
          {
            System.out.printf("Cannot add video to %s: Video already added\n", playlistName);
          }
        }
        else
        {
          System.out.printf("Cannot add video to %s: Video is currently flagged (reason: %s)\n", playlistName, selectedVideo.getFlagReason());
        }
      }
      else
      {
        System.out.printf("Cannot add video to %s: Video does not exist\n", playlistName);
      }
    }
    else
    {
       System.out.printf("Cannot add video to %s: Playlist does not exist\n", playlistName);
    }
  }

  public void showAllPlaylists() {
    if(!videoPlayLists.isEmpty())
    {
      System.out.println("Showing all playlists:");
      for(VideoPlaylist vPlaylist: videoPlayLists)
      {
        System.out.printf("%s\n", vPlaylist.getTitle());
      }
    }
    else
    {
      System.out.println("No playlists exist yet");
    }
  }

  public void showPlaylist(String playlistName) {
    if(checkIfPlaylistExists(playlistName))
    {
      System.out.printf("Showing playlist: %s\n", playlistName);
      VideoPlaylist selectVideoPlaylist = getVideoPlaylist(playlistName);
      if(selectVideoPlaylist.getVideos().size() > 0)
      {
        for(Video v: selectVideoPlaylist.getVideos())
        {
          String flagtext = "";
          if(v.getFlagStatus())
          {
            flagtext = String.format("- FLAGGED (reason: %s)", v.getFlagReason());
          }
          System.out.printf("%s (%s) %s %s\n", v.getTitle(), v.getVideoId(), v.getTags().toString().replace("," , ""), flagtext);
        }
      }
      else
      {
        System.out.println("No videos here yet");
      }
    }
    else
    {
      System.out.printf("Cannot show playlist %s: Playlist does not exist\n", playlistName);
    }
  }

  public void removeFromPlaylist(String playlistName, String videoId) {
    if(checkIfPlaylistExists(playlistName))
    {
      VideoPlaylist selectVideoPlaylist = getVideoPlaylist(playlistName);
      if(checkIfVideoExists(videoId, videoLibrary.getVideos()))
      {
        if(checkIfVideoExists(videoId, selectVideoPlaylist.getVideos()))
        {
          Video selectedVideo = videoLibrary.getVideo(videoId);
          selectVideoPlaylist.getVideos().remove(selectedVideo);
          System.out.printf("Removed video from %s: %s\n", playlistName, selectedVideo.getTitle());
        }
        else
        {
          System.out.printf("Cannot remove video from %s: Video is not in playlist\n", playlistName);
        }
      }
      else
      {
        System.out.printf("Cannot remove video from %s: Video does not exist\n", playlistName);
      }
    }
    else
    {
      System.out.printf("Cannot remove video from %s: Playlist does not exist\n", playlistName);
    }
  }

  public void clearPlaylist(String playlistName) {
    if(checkIfPlaylistExists(playlistName))
    {
      VideoPlaylist selectVideoPlaylist = getVideoPlaylist(playlistName);
      selectVideoPlaylist.getVideos().clear();
      System.out.printf("Successfully removed all videos from %s\n", playlistName);
    }
    else
    {
      System.out.printf("Cannot clear playlist %s: Playlist does not exist\n", playlistName);
    }
  }

  public void deletePlaylist(String playlistName) {
    if(checkIfPlaylistExists(playlistName))
    {
      VideoPlaylist selectVideoPlaylist = getVideoPlaylist(playlistName);
      videoPlayLists.remove(selectVideoPlaylist);
      System.out.printf("Deleted playlist: %s\n", playlistName);
    }
    else
    {
      System.out.printf("Cannot delete playlist %s: Playlist does not exist\n", playlistName);
    }
  }

  private void displayShortList(List<Video> videosShortList, String searchTerm){
      videosShortList.sort((v1, v2) -> v1.getTitle().compareTo(v2.getTitle()));

      System.out.printf("Here are the results for %s:\n", searchTerm);
      for(int i = 0; i < videosShortList.size(); i++)
      {
        Video v = videosShortList.get(i);
        System.out.printf("%d) %s (%s) %s\n", i+1, v.getTitle(), v.getVideoId(), v.getTags().toString().replace("," , ""));
      }
  }

  private void playVideoFromShortList(List<Video> videosShortList)
  {
    System.out.println(new StringBuilder("Would you like to play any of the above?")
      .append(" If yes, specify the number of the video.\n")
      .append("If your answer is not a valid number, we will assume it's a no.").toString());
    Scanner scanner = new Scanner(System.in);
    try
    {
      int videoNum = Integer.parseInt(scanner.nextLine());
      if(videoNum > 0 && videoNum <= videosShortList.size())
      {
        Video selectedVideo = videosShortList.get(videoNum - 1);
        playVideo(selectedVideo.getVideoId());
      }
    }
    catch(NumberFormatException e)
    {
      //display nothing
    }
  }

  public void searchVideos(String searchTerm) {
    List<Video> videosShortList = new LinkedList<>();
    if(!videoLibrary.getVideos().isEmpty())
    {
      for(Video v: videoLibrary.getVideos())
      {
        if(Pattern.compile(Pattern.quote(searchTerm), Pattern.CASE_INSENSITIVE).matcher(v.getTitle()).find() && v.getFlagStatus() == false)
        {
          videosShortList.add(v);
        }
      }

      if(!videosShortList.isEmpty())
      {
        displayShortList(videosShortList, searchTerm);
        playVideoFromShortList(videosShortList);
      }
      else
      {
        System.out.printf("No search results for %s\n", searchTerm);
      }
    }
  }

  public void searchVideosWithTag(String videoTag) {
    List<Video> videosShortList = new LinkedList<>();
    if(!videoLibrary.getVideos().isEmpty())
    {
      for(Video v: videoLibrary.getVideos())
      {
        for(String tag: v.getTags())
        {
          if(tag.equalsIgnoreCase(videoTag) && v.getFlagStatus() == false)
          {
            videosShortList.add(v);
          }
        }
      }
      if(!videosShortList.isEmpty())
      {
        displayShortList(videosShortList, videoTag);
        playVideoFromShortList(videosShortList);
      }
      else
      {
        System.out.printf("No search results for %s\n", videoTag);
      }
    }
  }

  public void flagVideo(String videoId) {
    if(!videoLibrary.getVideos().isEmpty())
    {
      if(checkIfVideoExists(videoId, videoLibrary.getVideos()))
      {
        Video selectedVideo = videoLibrary.getVideo(videoId);
        if(selectedVideo.getFlagStatus() == false)
        {
          selectedVideo.setFlagStatus(true);
          if(currentVideo != null && currentVideo == selectedVideo)
          {
            System.out.printf("Stopping video: %s\n", currentVideo.getTitle());
            currentVideo = null;
          }
          System.out.printf("Successfully flagged video: %s (reason: %s)\n", selectedVideo.getTitle(), selectedVideo.getFlagReason());
        }
        else
        {
          System.out.println("Cannot flag video: Video is already flagged");
        }
      }
      else
      {
        System.out.println("Cannot flag video: Video does not exist");
      }
    }
  }

  public void flagVideo(String videoId, String reason) {
    if(!videoLibrary.getVideos().isEmpty())
    {
      if(checkIfVideoExists(videoId, videoLibrary.getVideos()))
      {
        Video selectedVideo = videoLibrary.getVideo(videoId);
        if(selectedVideo.getFlagStatus() == false)
        {
          selectedVideo.setFlagStatus(true);
          selectedVideo.setFlagReason(reason);
          if(currentVideo != null && currentVideo == selectedVideo)
          {
            System.out.printf("Stopping video: %s\n", currentVideo.getTitle());
            currentVideo = null;
          }
          System.out.printf("Successfully flagged video: %s (reason: %s)\n", selectedVideo.getTitle(), selectedVideo.getFlagReason());
        }
        else
        {
          System.out.println("Cannot flag video: Video is already flagged");
        }
      }
      else
      {
        System.out.println("Cannot flag video: Video does not exist");
      }
    }
  }

  public void allowVideo(String videoId) {
    if(!videoLibrary.getVideos().isEmpty())
    {
      if(checkIfVideoExists(videoId, videoLibrary.getVideos()))
      {
        Video selectedVideo = videoLibrary.getVideo(videoId);
        if(selectedVideo.getFlagStatus())
        {
          selectedVideo.setFlagStatus(false);
          System.out.printf("Successfully removed flag from video: %s\n", selectedVideo.getTitle());
        }
        else
        {
          System.out.println("Cannot remove flag from video: Video is not flagged");
        }
      }
      else
      {
        System.out.println("Cannot remove flag from video: Video does not exist");
      }
    }
  }
}