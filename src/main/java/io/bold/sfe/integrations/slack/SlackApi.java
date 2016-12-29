package io.bold.sfe.integrations.slack;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public final class SlackApi {

  public static class EmptyResponse {
    public boolean ok;
  }

  public static class Authenticated {
    String token;
  }

  public static class Team {
    public String id;
    public String name;
  }

  public static class User {
    public String name;
    public String id;
    public String email;
    public String image_512;
  }

  public static class Value {
    public String value;
    public String creator;
    public long lastSet;
  }

  public static class AttachmentField {
    public String title;
    public String value;
    @JsonProperty("short") public boolean isShort;
  }

  public static class Attachment {
    public String fallback;
    public String pretext;
    public String color;
    @JsonProperty("author_name") public String authorName;
    @JsonProperty("author_link") public String authorLink;
    @JsonProperty("author_icon") public String authorIcon;
    public String title;
    @JsonProperty("title_link") public String titleLink;
    public String text;
    public List<AttachmentField> fields = new ArrayList<>();
    @JsonProperty("image_url") public String imageUrl;
    @JsonProperty("thumb_url") public String thumbUrl;
    public String footer;
    @JsonProperty("footer_icon") public String footerIcon;
    public long ts;
  }

  public static class Message {
    public String channel;
    public String text;
    @JsonProperty("username") public String userName;
    public String parse;
    public boolean linkNames;
    public boolean unfurlLinks;
    public boolean unfurlMedia;
    public boolean asUser;
    @JsonProperty("icon_url") public String iconUrl;
    @JsonProperty("icon_emoji") public String iconEmoji;
  }

  public static class PrivateGroup {
    public String id;
    public String name;
    public long created;
    public String creator;
    public Value topic;
    public Value purpose;
    @JsonProperty("is_archived") public boolean isArchived;

    public boolean isDm() {
      return name.startsWith("mpdm-");
    }
  }

  public static class PublicChannel {
    public String id;
    public String name;
    public long created;
    public String creator;
    public Value topic;
    public Value purpose;
    @JsonProperty("is_archived") public boolean isArchived;
    @JsonProperty("is_member") public boolean isMember;
    @JsonProperty("is_general") public boolean isGeneral;
  }

  public static String OAUTH_ACCESS_METHOD = "oauth.access";

  public static class OAuthAccessResponse {
    @JsonProperty("access_token") public String accessToken;
    public String scope;
    public User user;
    public Team team;
    @JsonProperty("user_id") public String userId;
    @JsonProperty("team_name") public String teamName;
    @JsonProperty("team_id") public String teamId;
  }

  public static String GROUPS_LIST_METHOD = "groups.list";

  public static class GroupsListResponse {
    public List<PrivateGroup> groups = new ArrayList<>();
  }

  public static String CHANNELS_LIST_METHOD = "channels.list";

  public static class ChannelsListResponse {
    public List<PublicChannel> channels = new ArrayList<>();
  }

  public static String POST_MESSAGE_METHOD = "chat.postMessage";

  public static String REVOKE_TOKEN_METHOD = "auth.revoke";

  public static class RevokeTokenResponse {
    public boolean revoked;
  }

  public static class Empty {}

  private SlackApi() {}

  public class Group {
  }
}
