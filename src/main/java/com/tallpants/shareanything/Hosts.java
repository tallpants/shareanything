package com.tallpants.shareanything;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class Hosts {
  public static URL imgur(File file) throws UnirestException {
    String imgurClientID = "9c65f969001905d";
    HttpResponse<JsonNode> response = Unirest.post("https://api.imgur.com/3/image")
      .header("authorization", "Client-ID " + imgurClientID)
      .field("image", file)
      .asJson();

    JSONObject obj = response.getBody().getObject();

    try {
      return new URL(obj.getJSONObject("data").getString("link"));
    } catch (MalformedURLException e) {
      return null;
    }
  }

  public static URL gist(File file) throws UnirestException, IOException {
    String fileContents = FileUtils.readFileToString(file, "UTF-8");

    /*
    {
      "public": true,
      "files": {
        "fileName": {
          "content": "File content"
        }
      }
    }
     */
    JSONObject body = new JSONObject();
      JSONObject files = new JSONObject();
       JSONObject fileName = new JSONObject();

        fileName.put("content", fileContents);
      files.put(file.getName(), fileName);
    body.put("files", files);
    body.put("public", "true");

    HttpResponse<JsonNode> response = Unirest.post("https://api.github.com/gists")
      .header("user-agent", "tallpants")
      .header("content-type", "application/json")
      .body(body.toString())
      .asJson();

    JSONObject obj = response.getBody().getObject();

    try {
      return new URL(obj.getString("html_url"));
    } catch (MalformedURLException e) {
      return null;
    }
  }
}