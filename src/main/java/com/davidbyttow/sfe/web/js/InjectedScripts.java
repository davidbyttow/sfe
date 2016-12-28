package com.davidbyttow.sfe.web.js;

import com.davidbyttow.sfe.integrations.fullstory.FullstoryConfig;
import com.davidbyttow.sfe.integrations.google.GoogleAnalyticsConfig;
import com.davidbyttow.sfe.integrations.mixpanel.MixpanelConfig;

public final class InjectedScripts {

  public static String forGoogleAnalytics(GoogleAnalyticsConfig ga) {
    return String.format(
      "(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){\n" +
        "(i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),\n" +
        "m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)\n" +
        "})(window,document,'script','https://www.google-analytics.com/analytics.js','__ga');\n" +
        "__ga('create', '%s');\n", ga.accountId);
  }

  public static String forFullstory(FullstoryConfig fullstory) {
    return String.format(
      "window['_fs_debug'] = false;\n" +
        "window['_fs_host'] = 'www.fullstory.com';\n" +
        "window['_fs_org'] = '%s';\n" +
        "window['_fs_namespace'] = 'FS';\n" +
        "(function(m,n,e,t,l,o,g,y){\n" +
        "  if (e in m && m.console && m.console.log) { m.console.log('FullStory namespace conflict. Please set window[\"_fs_namespace\"].'); return;}\n" +
        "  g=m[e]=function(a,b){g.q?g.q.push([a,b]):g._api(a,b);};g.q=[];\n" +
        "  o=n.createElement(t);o.async=1;o.src='https://'+_fs_host+'/s/fs.js';\n" +
        "  y=n.getElementsByTagName(t)[0];y.parentNode.insertBefore(o,y);\n" +
        "  g.identify=function(i,v){g(l,{uid:i});if(v)g(l,v)};g.setUserVars=function(v){g(l,v)};\n" +
        "  g.identifyAccount=function(i,v){o='account';v=v||{};v.acctId=i;g(o,v)};\n" +
        "  g.clearUserCookie=function(c,d,i){if(!c || document.cookie.match('fs_uid=[^;`]*`[^;`]*`[^;`]*`')){\n" +
        "  d=n.domain;while(1){n.cookie='fs_uid=;domain='+d+\n" +
        "  ';path=/;expires='+new Date(0);i=d.indexOf('.');if(i<0)break;d=d.slice(i+1)}}};\n" +
        "})(window,document,window['_fs_namespace'],'script','user');\n", fullstory.orgId);
  }

  public static String forMixpanel(MixpanelConfig mixpanelConfig) {
    return String.format(
      "(function(e,a){if(!a.__SV){var b=window;try{var c,l,i,j=b.location,g=j.hash;c=function(a,b){return(l=a.match(RegExp(b+\"=([^&]*)\")))?l[1]:null};g&&c(g,\"state\")&&(i=JSON.parse(decodeURIComponent(c(g,\"state\"))),\"mpeditor\"===i.action&&(b.sessionStorage.setItem(\"_mpcehash\",g),history.replaceState(i.desiredHash||\"\",e.title,j.pathname+j.search)))}catch(m){}var k,h;window.mixpanel=a;a._i=[];a.init=function(b,c,f){function e(b,a){var c=a.split(\".\");2==c.length&&(b=b[c[0]],a=c[1]);b[a]=function(){b.push([a].concat(Array.prototype.slice.call(arguments,\n" +
        "0)))}}var d=a;\"undefined\"!==typeof f?d=a[f]=[]:f=\"mixpanel\";d.people=d.people||[];d.toString=function(b){var a=\"mixpanel\";\"mixpanel\"!==f&&(a+=\".\"+f);b||(a+=\" (stub)\");return a};d.people.toString=function(){return d.toString(1)+\".people (stub)\"};k=\"disable time_event track track_pageview track_links track_forms register register_once alias unregister identify name_tag set_config reset people.set people.set_once people.increment people.append people.union people.track_charge people.clear_charges people.delete_user\".split(\" \");\n" +
        "for(h=0;h<k.length;h++)e(d,k[h]);a._i.push([b,c,f])};a.__SV=1.2;b=e.createElement(\"script\");b.type=\"text/javascript\";b.async=!0;b.src=\"undefined\"!==typeof MIXPANEL_CUSTOM_LIB_URL?MIXPANEL_CUSTOM_LIB_URL:\"file:\"===e.location.protocol&&\"//cdn.mxpnl.com/libs/mixpanel-2-latest.min.js\".match(/^\\/\\//)?\"https://cdn.mxpnl.com/libs/mixpanel-2-latest.min.js\":\"//cdn.mxpnl.com/libs/mixpanel-2-latest.min.js\";c=e.getElementsByTagName(\"script\")[0];c.parentNode.insertBefore(b,c)}})(document,window.mixpanel||[]);\n" +
        "mixpanel.init('%s');\n", mixpanelConfig.token);
  }

  private InjectedScripts() {}
}
