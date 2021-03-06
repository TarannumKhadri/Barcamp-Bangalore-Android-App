/*
 * Copyright (C) 2012 Saurabh Minni <http://100rabh.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bangalore.barcamp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.bangalore.barcamp.data.BarcampBangalore;
import com.bangalore.barcamp.data.BarcampData;
import com.bangalore.barcamp.data.BarcampUserScheduleData;
import com.bangalore.barcamp.data.Session;
import com.bangalore.barcamp.data.Slot;

public class BCBUtils {

	private static final int MAX_LOG = 2000;
	private static final String BARCAMP_SCHEDULE_JSON = "http://barcampbangalore.org/bcb/schadmin/android.json";
	private static final String BCB_LOCATION_MAPS_URL = "https://www.google.co.in/maps/place/CMRIT/@12.9663985,77.7121306,17z/data=!4m6!1m3!3m2!1s0x0000000000000000:0x7896436c100b0272!2sCMRIT!3m1!1s0x0000000000000000:0x7896436c100b0272?hl=en";
	protected static final int START_SCHEDULE = 100;
	protected static final int START_ABOUT = 101;
	protected static final int START_SETTINGS = 102;
	protected static final int START_SHARE = 103;
	protected static final int START_BCB12_TWEETS = 104;
	protected static final int START_BCB_UPDATES = 105;
	private static final String BCB_USER_SCHEDULE_URL = "http://barcampbangalore.org/bcb/wp-android_helper.php?action=getuserdata&userid=%s&userkey=%s";
	protected static final int START_INTERNAL_VENUE = 106;

	//
	// public static void createActionBarOnActivity(final Activity activity) {
	// createActionBarOnActivity(activity, false);
	// }

	//
	// public static void createActionBarOnActivity(final Activity activity,
	// boolean isHome) {
	// // ******** Start of Action Bar configuration
	// ActionBar actionbar = (ActionBar) activity
	// .findViewById(R.id.actionBar1);
	// actionbar.setHomeLogo(R.drawable.home);
	// actionbar.setHomeAction(new Action() {
	// @Override
	// public void performAction(View view) {
	// ((SlidingMenuActivity) activity).toggle();
	// }
	//
	// @Override
	// public int getDrawable() {
	// return R.drawable.home;
	// }
	// });
	//
	// actionbar.setTitle(R.string.app_title_text);
	// TextView logo = (TextView) activity.findViewById(R.id.actionbar_title);
	// Shader textShader = new LinearGradient(0, 0, 0, logo.getHeight(),
	// new int[] { Color.WHITE, 0xff999999 }, null, TileMode.CLAMP);
	// logo.getPaint().setShader(textShader);
	// actionbar.setOnTitleClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	//
	// }
	// });
	// // ******** End of Action Bar configuration
	//
	// }
	//
	// // public static Action createShareAction(Activity activity) {
	// IntentAction shareAction = new IntentAction(activity,
	// createShareIntent(activity), R.drawable.share_icon);
	//
	// return shareAction;
	// }
	//
	// public static Intent createShareIntent(Activity activity) {
	// Intent intent = new Intent(activity, ShareActivity.class);
	// return intent;
	//
	// }

	public static PendingIntent createPendingIntentForID(Context context,
			String id, int slot, int session) {
		Intent intent = new Intent(context, SessionAlarmIntentService.class);
		intent.putExtra(SessionAlarmIntentService.SESSION_ID, id);
		intent.putExtra(SessionAlarmIntentService.EXTRA_SLOT_POS, slot);
		intent.putExtra(SessionAlarmIntentService.EXTRA_SESSION_POSITION,
				session);
		int idInt = Integer.parseInt(id);
		PendingIntent pendingIntent = PendingIntent.getService(context, idInt,
				intent, PendingIntent.FLAG_ONE_SHOT);
		return pendingIntent;
	}

	public static Boolean updateContextWithBarcampData(Context context) {
		Boolean retVal = false;
		BufferedReader in = null;
		try {
			URL url = new URL(BARCAMP_SCHEDULE_JSON);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000 /* milliseconds */);
			conn.setConnectTimeout(15000 /* milliseconds */);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			// Starts the query
			conn.connect();
			int response = conn.getResponseCode();
//						Log.d(DEBUG_TAG, "The response is: " + response);
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

//			HttpClient client = new DefaultHttpClient();
//			HttpUriRequest request = new HttpGet(BARCAMP_SCHEDULE_JSON);
//			HttpResponse response = client.execute(request);
//			in = new BufferedReader(new InputStreamReader(response.getEntity()
//					.getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			in.close();
			String page = sb.toString();
//			 page =
//			 "{\"status\":\"have stuff\",\"version\":34,\"slots\":[{\"type\":\"fixed\",\"startTime\":\"800\",\"endTime\":\"900\",\"name\":\"Registration\",\"id\":1},{\"type\":\"fixed\",\"startTime\":\"900\",\"endTime\":\"930\",\"name\":\"Introduction\",\"id\":2},{\"type\":\"session\",\"startTime\":\"930\",\"endTime\":\"1015\",\"name\":\"Slot 1\",\"id\":3,\"sessions\":[{\"id\":2852,\"title\":\"Big Data Basics\",\"description\":\"Big Data, Data Science &amp; Analytics. Data Analytics Life cycle. Analytics technology &amp; tools – Map Reduce, Hadoop. Methods to apply this knowledge to real work business challenges. <a href=\\\"http://barcampbangalore.org/bcb/bcb14/big-data-basics\\\">Read more</a>\",\"permalink\":\"http://barcampbangalore.org/bcb/bcb14/big-data-basics\",\"time\":\"9:30AM - 10:15AM\",\"location\":\"Asteroids\",\"presenter\":\"Viji\",\"photo\":\"http://graph.facebook.com/1255925592/picture?type=square\",\"category\":\"Technology\",\"level\":\"Intro/101\",\"color\":\"#AA8E5D\"},{\"id\":2799,\"title\":\"Making Music with Free Software Tools\",\"description\":\"Ever wanted to create some cool music or tunes? Let&#8217;s do this! Software Instruments:  Audacity and Hydrogen&#8230; and some web apps and Chrome extensions. No expertise of  Sa Re Ga Ma Pa needed. What to Bring:  Laptop or Smartphone. Any tune you are/were/have been humming. Level: Beginner <a href=\\\"http://barcampbangalore.org/bcb/bcb14/making-music-with-free-software-tools\\\">Read more</a>\",\"permalink\":\"http://barcampbangalore.org/bcb/bcb14/making-music-with-free-software-tools\",\"time\":\"9:30AM - 10:15AM\",\"location\":\"Battleship\",\"presenter\":\"Vivekk\",\"photo\":\"http://0.gravatar.com/avatar/4c776f36af429e9382ea60c3e43d6cb6?s=96&amp;d=wavatar&amp;r=G\",\"category\":\"Rest of The World\",\"level\":\"Intro/101\",\"color\":\"#756480\"},{\"id\":2930,\"title\":\"Evaluating startup &amp; technology events!\",\"description\":\"There are more than 100 startup and technology events happening across the country every week with more than a dozen taking place in Bangalore. From an informal meetup to a pitching session to a mighty conference, how to decide which one is worth your time and money? What all parameter should be considered to take [&hellip;] <a href=\\\"http://barcampbangalore.org/bcb/bcb14/evaluating-startup-technology-events-happening-around-you-to-attend\\\">Read more</a>\",\"permalink\":\"http://barcampbangalore.org/bcb/bcb14/evaluating-startup-technology-events-happening-around-you-to-attend\",\"time\":\"9:30AM - 10:15AM\",\"location\":\"Contra\",\"presenter\":\"Alok\",\"photo\":\"http://0.gravatar.com/avatar/4834adf6a2cc8d5bc3bc0f82e7066422?s=96&amp;d=wavatar&amp;r=G\",\"category\":\"Entrepreneurship\",\"level\":\"Discussion\",\"color\":\"#EB9E58\"},{\"id\":2974,\"title\":\"Selenium Simple Test &#8211; a python web framework for functional Testing\",\"description\":\"To write functional test cases for web app, selenium is one of the well known tool across the industry. SST or Selenium Simple Test is framework, written on top of Selenium Web driver, which uses python to write test cases. This is how, one can avoid using Java and still can write efficient web automation <a href=\\\"http://barcampbangalore.org/bcb/bcb14/selenium-simple-test-a-python-web-framework-for-functional-testing\\\">Read more</a>\",\"permalink\":\"http://barcampbangalore.org/bcb/bcb14/selenium-simple-test-a-python-web-framework-for-functional-testing\",\"time\":\"9:30AM - 10:15AM\",\"location\":\"Diablo\",\"presenter\":\"fagun\",\"photo\":\"http://0.gravatar.com/avatar/0e05524b54bbb749c0c29087f09ef618?s=96&amp;d=wavatar&amp;r=G\",\"category\":\"Mobile and Web\",\"level\":\"In-depth talks\",\"color\":\"#777DD1\"},{\"id\":2911,\"title\":\"Profiling and Benchmarking Devices\",\"description\":\"For starters, lets talk about what is profiling and benchmarking, why you need it, what those numbers mean which you get after profiling and most important: with current and future devices bumped up with more processor power and extra memory, how do we compare and set benchmarking standards. <a href=\\\"http://barcampbangalore.org/bcb/bcb14/profiling-and-benchmarking-devices\\\">Read more</a>\",\"permalink\":\"http://barcampbangalore.org/bcb/bcb14/profiling-and-benchmarking-devices\",\"time\":\"9:30AM - 10:15AM\",\"location\":\"Everquest\",\"presenter\":\"Daaku\",\"photo\":\"http://1.gravatar.com/avatar/7d8d90724c549fd22e200393932099b5?s=96&amp;d=wavatar&amp;r=G\",\"category\":\"Scaling and Infrastructure\",\"level\":\"Discussion\",\"color\":\"#879958\"},{\"id\":3162,\"title\":\"Solving for Road Realities, Smartly!\",\"description\":\"Solving ground transportation with technology <a href=\\\"http://barcampbangalore.org/bcb/bcb14/solving-for-road-realities-smartly\\\">Read more</a>\",\"permalink\":\"http://barcampbangalore.org/bcb/bcb14/solving-for-road-realities-smartly\",\"time\":\"9:30AM - 10:15AM\",\"location\":\"Fable\",\"presenter\":\"amanmanglik\",\"photo\":\"http://graph.facebook.com/694556501/picture?type=square\",\"category\":\"Bangalore & Lifestyle\",\"level\":\"Discussion\",\"color\":\"#7CB4F1\"}]},{\"type\":\"session\",\"startTime\":\"1030\",\"endTime\":\"1115\",\"name\":\"Slot 2\",\"id\":4,\"sessions\":[{\"id\":2752,\"title\":\"Introduction to Browser Internals\",\"description\":\"You might have written lots of HTML, CSS and Javascript code. Have you ever wondered how it works together? This session focuses on the basics of Internal DOM Structure Layout Engine JavaScript Engine Various Developer Tools available in the browsers How to access/change the DOM objects and much more fun demos&#8230;. Presentation: http://www.slideshare.net/sivasubramaniam3/introduction-to-browser-internals <a href=\\\"http://barcampbangalore.org/bcb/bcb14/introduction-to-browser-internals\\\">Read more</a>\",\"permalink\":\"http://barcampbangalore.org/bcb/bcb14/introduction-to-browser-internals\",\"time\":\"10:30AM - 11:15AM\",\"location\":\"Asteroids\",\"presenter\":\"sivaa\",\"photo\":\"http://1.gravatar.com/avatar/f8dc8fabdbce2f177e3f5029775a0587?s=96&amp;d=wavatar&amp;r=G\",\"category\":\"Mobile and Web\",\"level\":\"Intro/101\",\"color\":\"#777DD1\"},{\"id\":2838,\"title\":\"Little Known Secrets to convey anything &amp; everything Visually !!\",\"description\":\"Whether you are an entrepreneur (or) a techie working for a company (or) a Manager handling a team (or) a student, its really important that you communicate effectively to your audience. Text is diminishing. The most successful ones &#8211; right from Marketing Guru Seth Godin, to Search Major Google, have reduced the usage of text [&hellip;] <a href=\\\"http://barcampbangalore.org/bcb/bcb14/little-known-secrets-to-convey-anything-everything-visually\\\">Read more</a>\",\"permalink\":\"http://barcampbangalore.org/bcb/bcb14/little-known-secrets-to-convey-anything-everything-visually\",\"time\":\"10:30AM - 11:15AM\",\"location\":\"Battleship\",\"presenter\":\"rameshkumarv\",\"photo\":\"http://0.gravatar.com/avatar/c924a8e4258efbc7d88797bd26444e1a?s=96&amp;d=wavatar&amp;r=G\",\"category\":\"Rest of The World\",\"level\":\"Intro/101\",\"color\":\"#756480\"},{\"id\":2892,\"title\":\"Know your Rights &#8211; Photography laws in India.\",\"description\":\"What are the Indian photography laws? What are your rights over the photos you take? What is the Legality of photography in public places? <a href=\\\"http://barcampbangalore.org/bcb/bcb14/know-your-rights-photography-laws-in-india\\\">Read more</a>\",\"permalink\":\"http://barcampbangalore.org/bcb/bcb14/know-your-rights-photography-laws-in-india\",\"time\":\"10:30AM - 11:15AM\",\"location\":\"Contra\",\"presenter\":\"Ratzzz\",\"photo\":\"http://1.gravatar.com/avatar/5db2fcd992206e2fd7627a85ee9b4d53?s=96&amp;d=wavatar&amp;r=G\",\"category\":\"Bangalore & Lifestyle\",\"level\":\"Discussion\",\"color\":\"#7CB4F1\"},{\"id\":2923,\"title\":\"Introduction to Mobile Testing\",\"description\":\"How about getting our app tested before we send it to the store? Join me in understanding the importance of testing a mobile app, and the &#8220;Why&#8221;, &#8220;What&#8221; and &#8220;How&#8221; of testing a Mobile application. It is going to be very informative! So let&#8217;s get started with some of the tips and tricks to test [&hellip;] <a href=\\\"http://barcampbangalore.org/bcb/bcb14/introduction-to-mobile-testing\\\">Read more</a>\",\"permalink\":\"http://barcampbangalore.org/bcb/bcb14/introduction-to-mobile-testing\",\"time\":\"10:30AM - 11:15AM\",\"location\":\"Diablo\",\"presenter\":\"abilash\",\"photo\":\"http://0.gravatar.com/avatar/656abcb2ce2fa83bc501da062d75fe94?s=96&amp;d=wavatar&amp;r=G\",\"category\":\"Mobile and Web\",\"level\":\"Intro/101\",\"color\":\"#777DD1\"},{\"id\":2992,\"title\":\"Meet Open Source Portal and Integration\",\"description\":\"Introduction to open source portals like Liferay and integration of related frameworks and application, rapid development of portals in java. <a href=\\\"http://barcampbangalore.org/bcb/bcb14/meet-open-source-portal-and-integration\\\">Read more</a>\",\"permalink\":\"http://barcampbangalore.org/bcb/bcb14/meet-open-source-portal-and-integration\",\"time\":\"10:30AM - 11:15AM\",\"location\":\"Everquest\",\"presenter\":\"chandansharma\",\"photo\":\"http://0.gravatar.com/avatar/e889c5b6753c71361398ebd89ca06d1c?s=96&amp;d=wavatar&amp;r=G\",\"category\":\"Scaling and Infrastructure\",\"level\":\"Discussion\",\"color\":\"#879958\"},{\"id\":3121,\"title\":\"Interaction Design : Cognitive Walkthrough + Neilson&#8217;s Heuristics\",\"description\":\"With the increase in focus on user experience design, the need to evaluate design objectively is a concern. If we strip an app from its visual aesthetics we can explore its interaction design.Of various evaluation methods available Cognitive walkthrough is cost effective.The cognitive walkthrough is a formalized way of imagining people&#8217;s thoughts and actions when [&hellip;] <a href=\\\"http://barcampbangalore.org/bcb/bcb14/interaction-design-cognitive-walkthrough\\\">Read more</a>\",\"permalink\":\"http://barcampbangalore.org/bcb/bcb14/interaction-design-cognitive-walkthrough\",\"time\":\"10:30AM - 11:15AM\",\"location\":\"Fable\",\"presenter\":\"Vigneshwar Poojar\",\"photo\":\"http://graph.facebook.com/715642499/picture?type=square\",\"category\":\"Design\",\"level\":\"Intro/101\",\"color\":\"#D35D5D\"}]},{\"type\":\"session\",\"startTime\":\"1130\",\"endTime\":\"1215\",\"name\":\"Slot 3\",\"id\":5,\"sessions\":[{\"id\":2777,\"title\":\"Why you shouldn&#8217;t work at a startup\",\"description\":\"*Bait alert* So, I work at a startup that champions startups and have worked at startups all through my professional life. While a lot of people will tell you that working at a startup is the next best thing to being an entrepreneur, you need to know what you&#8217;re signing up for. This is going [&hellip;] <a href=\\\"http://barcampbangalore.org/bcb/bcb14/why-you-shouldnt-work-at-a-startup\\\">Read more</a>\",\"permalink\":\"http://barcampbangalore.org/bcb/bcb14/why-you-shouldnt-work-at-a-startup\",\"time\":\"11:30AM - 12:15AM\",\"location\":\"Asteroids\",\"presenter\":\"Raghu25289\",\"photo\":\"http://0.gravatar.com/avatar/a20d888a929a9290e468708951f9c6db?s=96&amp;d=wavatar&amp;r=G\",\"category\":\"Design\",\"level\":\"Intro/101\",\"color\":\"#D35D5D\"},{\"id\":2789,\"title\":\"OpenStack in action &#8211; Powering IaaS Cloud\",\"description\":\"Recently OpenStack celebrated 3rd anniversary.Best known for driving cloud innovation and accelerating cloud implementation. OpenStack India user group is emerging as one of the biggest group across globe. This session is purely for beginner to understand basics of OpenStack. - .History and Projects - How to contribute - Success stories Join us and get involved [&hellip;] <a href=\\\"http://barcampbangalore.org/bcb/bcb14/openstack-in-action-powering-iaas-cloud\\\">Read more</a>\",\"permalink\":\"http://barcampbangalore.org/bcb/bcb14/openstack-in-action-powering-iaas-cloud\",\"time\":\"11:30AM - 12:15AM\",\"location\":\"Battleship\",\"presenter\":\"Sajid Akhtar\",\"photo\":\"http://graph.facebook.com/687812219/picture?type=square\",\"category\":\"Design\",\"level\":\"Intro/101\",\"color\":\"#D35D5D\"},{\"id\":3087,\"title\":\"How to Design a good User Interfaces\",\"description\":\"We ll taking up a real world problem and try design a UI with UX best practices. <a href=\\\"http://barcampbangalore.org/bcb/bcb14/design-user-interfaces\\\">Read more</a>\",\"permalink\":\"http://barcampbangalore.org/bcb/bcb14/design-user-interfaces\",\"time\":\"11:30AM - 12:15AM\",\"location\":\"Contra\",\"presenter\":\"arunmaroon\",\"photo\":\"http://graph.facebook.com/666190459/picture?type=square\",\"category\":\"Design\",\"level\":\"Intro/101\",\"color\":\"#D35D5D\"},{\"id\":2942,\"title\":\"I&#8217;m a Bloody Hacker! Let the hacking begin.\",\"description\":\"Being a ETHICAL HACKER since 16, I have been hacking different applications to help product owners to fix the vulnerabilities before they get into brain of bad guys out there. I do not believe in &#8220;Ethical&#8221; or &#8220;Unethical&#8221;. I believe in just hacking (Because, ethical and unethical are perception of individual. That is all). What [&hellip;] <a href=\\\"http://barcampbangalore.org/bcb/bcb14/im-a-bloody-hacker-let-the-hacking-begin\\\">Read more</a>\",\"permalink\":\"http://barcampbangalore.org/bcb/bcb14/im-a-bloody-hacker-let-the-hacking-begin\",\"time\":\"11:30AM - 12:15AM\",\"location\":\"Diablo\",\"presenter\":\"BloodyHacker\",\"photo\":\"http://0.gravatar.com/avatar/6b7633fda88c71b4dc8831ae59b3c1b6?s=96&amp;d=wavatar&amp;r=G\",\"category\":\"Design\",\"level\":\"Intro/101\",\"color\":\"#D35D5D\"},{\"id\":3099,\"title\":\"Growing up as an Indian Gamer: An Insight\",\"description\":\"This session describes the change in life seen by the people who have been playing games since their childhood from glorious days of video games to early entry of computers. It also shows how India is depicted in games and what can be done to change that. <a href=\\\"http://barcampbangalore.org/bcb/bcb14/growing-up-as-an-indian-gamer-an-insight\\\">Read more</a>\",\"permalink\":\"http://barcampbangalore.org/bcb/bcb14/growing-up-as-an-indian-gamer-an-insight\",\"time\":\"11:30AM - 12:15AM\",\"location\":\"Everquest\",\"presenter\":\"Pratik Anand\",\"photo\":\"http://graph.facebook.com/1075546128/picture?type=square\",\"category\":\"Design\",\"level\":\"Intro/101\",\"color\":\"#D35D5D\"},{\"id\":3097,\"title\":\"SWeeTing Cultural Heritage\",\"description\":\"We demonstrate a set of reusable tools that we have developed to help the process of annotation and curation for cultural heritage and museum platforms. We start with customizing a set of tools for a given context and use it to demonstrate its utility in annotating a mural or another artefact with semantic labels. Then [&hellip;] <a href=\\\"http://barcampbangalore.org/bcb/bcb14/sweeting-cultural-heritage\\\">Read more</a>\",\"permalink\":\"http://barcampbangalore.org/bcb/bcb14/sweeting-cultural-heritage\",\"time\":\"11:30AM - 12:15AM\",\"location\":\"Fable\",\"presenter\":\"tbdinesh\",\"photo\":\"http://1.gravatar.com/avatar/b49473854236ff992023eea2aa0f1505?s=96&amp;d=wavatar&amp;r=G\",\"category\":\"Design\",\"level\":\"Intro/101\",\"color\":\"#D35D5D\"}]},{\"type\":\"fixed\",\"startTime\":\"1230\",\"endTime\":\"1330\",\"name\":\"Lunch\",\"id\":6},{\"type\":\"fixed\",\"startTime\":\"1330\",\"endTime\":\"1430\",\"name\":\"Techlash\",\"id\":7},{\"type\":\"session\",\"startTime\":\"1430\",\"endTime\":\"1515\",\"name\":\"Slot 4\",\"id\":8,\"sessions\":[{\"id\":2925,\"title\":\"Introduction to security testing/Ethical Hacking\",\"description\":\"Meet an ethical hacker while you are here at the Bar Camp Bangalore and learn what qualifies for a Hack? What goes on in the mind of an ethical hacker and a hacker? Learn new and the trending technologies to be right there at the right moment. Been Victimized? Learn what you could have done [&hellip;] <a href=\\\"http://barcampbangalore.org/bcb/bcb14/introduction-to-security-testingethical-hacking\\\">Read more</a>\",\"permalink\":\"http://barcampbangalore.org/bcb/bcb14/introduction-to-security-testingethical-hacking\",\"time\":\"2:30PM - 3:15PM\",\"location\":\"Asteroids\",\"presenter\":\"nagasahas\",\"photo\":\"http://1.gravatar.com/avatar/bacfe76c85d8b3efc8ae66c4e505e858?s=96&amp;d=wavatar&amp;r=G\",\"category\":\"Design\",\"level\":\"Intro/101\",\"color\":\"#D35D5D\"},{\"id\":2731,\"title\":\"How to keep Android open source\",\"description\":\"We keep hearing that Android is not really open source. The code of it available for all after its released. But there is a catch now. Google Play services. Google is moving a lot of its code, which could have been part of Android OS to Play services. Things like Location API&#8217;s, GCM, Games etc. [&hellip;] <a href=\\\"http://barcampbangalore.org/bcb/bcb14/how-to-keep-android-open-source\\\">Read more</a>\",\"permalink\":\"http://barcampbangalore.org/bcb/bcb14/how-to-keep-android-open-source\",\"time\":\"2:30PM - 3:15PM\",\"location\":\"Battleship\",\"presenter\":\"the100rabh\",\"photo\":\"http://1.gravatar.com/avatar/bb6caa13742a331aad8b493034663a64?s=96&amp;d=wavatar&amp;r=G\",\"category\":\"Design\",\"level\":\"Intro/101\",\"color\":\"#D35D5D\"},{\"id\":2767,\"title\":\"Product Management ABC and D\",\"description\":\"Intend to keep this session interactive, and primarily driven by participants. Go ahead and shoot your queries. And like they say, there are no stupid questions! Target Audience &#8211; Anyone with an interest in Product Management, be it aspirants, practitioners, or anyone with general interest are all welcome. <a href=\\\"http://barcampbangalore.org/bcb/bcb14/product-management-abc-and-d\\\">Read more</a>\",\"permalink\":\"http://barcampbangalore.org/bcb/bcb14/product-management-abc-and-d\",\"time\":\"2:30PM - 3:15PM\",\"location\":\"Contra\",\"presenter\":\"mtanwani\",\"photo\":\"http://0.gravatar.com/avatar/2b641429191e93db0ff15d6dccda76d8?s=96&amp;d=wavatar&amp;r=G\",\"category\":\"Design\",\"level\":\"Intro/101\",\"color\":\"#D35D5D\"},{\"id\":3035,\"title\":\"Setting Expectations and Making Money&#8230;\",\"description\":\"There are only two kinds of people in this world: &#8220;those who believe&#8221; and &#8220;those who make others believe&#8221;. And this is where all the power lies. <a href=\\\"http://barcampbangalore.org/bcb/bcb14/setting-expectations-and-making-money\\\">Read more</a>\",\"permalink\":\"http://barcampbangalore.org/bcb/bcb14/setting-expectations-and-making-money\",\"time\":\"2:30PM - 3:15PM\",\"location\":\"Diablo\",\"presenter\":\"brandbull\",\"photo\":\"http://1.gravatar.com/avatar/33628f6e2ae1f9e58a1826c2dfc11b45?s=96&amp;d=wavatar&amp;r=G\",\"category\":\"Design\",\"level\":\"Intro/101\",\"color\":\"#D35D5D\"},{\"id\":2934,\"title\":\"Value What you throw!\",\"description\":\"&#8220;Trash is India’s plague&#8221; While a lot of us know that it is important to manage our trash, only a few of us practice this. This is going to be a talk about why &amp; how as individuals we can stop poisoning our &#8220;Mother earth&#8221;. <a href=\\\"http://barcampbangalore.org/bcb/bcb14/value-what-you-throw\\\">Read more</a>\",\"permalink\":\"http://barcampbangalore.org/bcb/bcb14/value-what-you-throw\",\"time\":\"2:30PM - 3:15PM\",\"location\":\"Everquest\",\"presenter\":\"bhoomika\",\"photo\":\"http://0.gravatar.com/avatar/29f7e75063d9b0846f88c8670784ed97?s=96&amp;d=wavatar&amp;r=G\",\"category\":\"Design\",\"level\":\"Intro/101\",\"color\":\"#D35D5D\"},{\"id\":3118,\"title\":\"Accessibility  Beyond Guidelines\",\"description\":\"WCAG has become synonym of Accessibility but accessibility testing is more than guidelines. We should remember guidelines are just advice statement and not the Ru!es. The session is about conducting accessibility testing beyond guidelines. If you care about accessibility and don&#8217;t believe in guidelines or any ru!es, this session is for you. <a href=\\\"http://barcampbangalore.org/bcb/bcb14/accessibility-beyond-guidelines\\\">Read more</a>\",\"permalink\":\"http://barcampbangalore.org/bcb/bcb14/accessibility-beyond-guidelines\",\"time\":\"2:30PM - 3:15PM\",\"location\":\"Fable\",\"presenter\":\"Mohit Verma\",\"photo\":\"http://0.gravatar.com/avatar/a67daf4e277bef23880480eca1478449?s=96&amp;d=wavatar&amp;r=G\",\"category\":\"Design\",\"level\":\"Intro/101\",\"color\":\"#D35D5D\"}]},{\"type\":\"session\",\"startTime\":\"1530\",\"endTime\":\"1615\",\"name\":\"Slot 5\",\"id\":9,\"sessions\":[{\"id\":2759,\"title\":\"Why people Lie &#8211; A choice between being NICE and being REAL\",\"description\":\"Sounds like a simple question, yet we wonder why do people who appear nice in person and talk wisely become an entirely opposite person the moment they leave the room. Why can&#8217;t they be their true self while talking to you in the first place. Find answers to similar questions. and also find out where [&hellip;] <a href=\\\"http://barcampbangalore.org/bcb/bcb14/why-people-lie-a-choice-between-being-nice-and-being-real\\\">Read more</a>\",\"permalink\":\"http://barcampbangalore.org/bcb/bcb14/why-people-lie-a-choice-between-being-nice-and-being-real\",\"time\":\"3:30PM - 4:15PM\",\"location\":\"Asteroids\",\"presenter\":\"Aditya Bhushan Dwivedi\",\"photo\":\"http://graph.facebook.com/1216302536/picture?type=square\",\"category\":\"Design\",\"level\":\"Intro/101\",\"color\":\"#D35D5D\"},{\"id\":2714,\"title\":\"Stupidity on the Internet\",\"description\":\"We talk about stupid behaviour on the Internet <a href=\\\"http://barcampbangalore.org/bcb/bcb14/participating-in-qa-sites-2\\\">Read more</a>\",\"permalink\":\"http://barcampbangalore.org/bcb/bcb14/participating-in-qa-sites-2\",\"time\":\"3:30PM - 4:15PM\",\"location\":\"Battleship\",\"presenter\":\"sathyabhat\",\"photo\":\"http://graph.facebook.com/543508896/picture?type=square\",\"category\":\"Design\",\"level\":\"Intro/101\",\"color\":\"#D35D5D\"},{\"id\":3027,\"title\":\"Cooking your dev. &amp; prod. infrastructure in minutes with Chef\",\"description\":\"Managing servers and production deployments is part and parcel of today&#8217;s developer activity. In this session we will i. Introduction to Vagrant ii. Introduction to Chef iii. Dev Setup in minutes iv. Live demo of bringing up a basic image to deploy ready. Q&amp;A. <a href=\\\"http://barcampbangalore.org/bcb/bcb14/cooking-infrastructure-with-chef\\\">Read more</a>\",\"permalink\":\"http://barcampbangalore.org/bcb/bcb14/cooking-infrastructure-with-chef\",\"time\":\"3:30PM - 4:15PM\",\"location\":\"Contra\",\"presenter\":\"Ramjee\",\"photo\":\"http://1.gravatar.com/avatar/f894e77f943c94d00b1bfa72ccf84191?s=96&amp;d=wavatar&amp;r=G\",\"category\":\"Design\",\"level\":\"Intro/101\",\"color\":\"#D35D5D\"},{\"id\":2867,\"title\":\"Git for fun and productivity\",\"description\":\"Have you been using non-DVCS like CVS, SVN, etc.? Its time to move on &#8211; use Distributed Version Control System (DVCS) like Git, Hg, etc. If you&#8217;re already using Git do you know why you&#8217;re using Git? If not, this session is for you. First theory. In this talk Karthik would cite solid reasons to [&hellip;] <a href=\\\"http://barcampbangalore.org/bcb/bcb14/git-for-fun-and-productivity\\\">Read more</a>\",\"permalink\":\"http://barcampbangalore.org/bcb/bcb14/git-for-fun-and-productivity\",\"time\":\"3:30PM - 4:15PM\",\"location\":\"Diablo\",\"presenter\":\"Karthik Sirasanagandla\",\"photo\":\"http://1.gravatar.com/avatar/fb1d21e582835b53a7b19ffa7b6df6cb?s=96&amp;d=wavatar&amp;r=G\",\"category\":\"Design\",\"level\":\"Intro/101\",\"color\":\"#D35D5D\"},{\"id\":2928,\"title\":\"lend me your processors and I will give you a happier world!\",\"description\":\"by harvesting the power of grid computing, you too can help in finding solution to big, global problems of the world like •AIDS •Childhood Cancer •finding sources for Clean Energy and clean Water •finding cure for Schistosoma and many more. all you have to do is donate the unused power of your processor! join me [&hellip;] <a href=\\\"http://barcampbangalore.org/bcb/bcb14/lend-me-your-processors-and-i-will-give-you-a-happier-world\\\">Read more</a>\",\"permalink\":\"http://barcampbangalore.org/bcb/bcb14/lend-me-your-processors-and-i-will-give-you-a-happier-world\",\"time\":\"3:30PM - 4:15PM\",\"location\":\"Everquest\",\"presenter\":\"ruchir89\",\"photo\":\"http://0.gravatar.com/avatar/0a7d20e6af8fe6cf3285fd4ff911e8d6?s=96&amp;d=wavatar&amp;r=G\",\"category\":\"Design\",\"level\":\"Intro/101\",\"color\":\"#D35D5D\"},{\"id\":3142,\"title\":\"Finding patterns\",\"description\":\"Every thing in this world is related. This is hypothesis. My talk is about why I think every thing in this world is related. How is big data related in finding patterns. I will also speak on why I think data science has numerous possibilities. Then it&#8217;s on what is big data.Why learn it. <a href=\\\"http://barcampbangalore.org/bcb/bcb14/finding-patterns\\\">Read more</a>\",\"permalink\":\"http://barcampbangalore.org/bcb/bcb14/finding-patterns\",\"time\":\"3:30PM - 4:15PM\",\"location\":\"Fable\",\"presenter\":\"Vundemodalu Manjush\",\"photo\":\"https://2.gravatar.com/avatar/934548c48010f7af39a8f6fd5d8932f0?d=https%3A%2F%2Fidenticons.github.com%2F761db2faec7ba9bb33ba9ab7aa2eab2e.png\",\"category\":\"Design\",\"level\":\"Intro/101\",\"color\":\"#D35D5D\"}]},{\"type\":\"session\",\"startTime\":\"1630\",\"endTime\":\"1715\",\"name\":\"Slot 6\",\"id\":10,\"sessions\":[{\"id\":2764,\"title\":\"Corruption control using technology\",\"description\":\"Avoid the corruption and remove the black money concept in India using this technology we can trace all the transaction of the every citizen of the India. <a href=\\\"http://barcampbangalore.org/bcb/bcb14/corruption-control-using-technology\\\">Read more</a>\",\"permalink\":\"http://barcampbangalore.org/bcb/bcb14/corruption-control-using-technology\",\"time\":\"4:30PM - 5:15PM\",\"location\":\"Asteroids\",\"presenter\":\"Mahanthesh Shadakshari\",\"photo\":\"https://1.gravatar.com/avatar/d6131786a44801920e361004d2f4bd31?d=https%3A%2F%2Fidenticons.github.com%2Fbc74cd20471f9c8c9665bacef6deb51c.png\",\"category\":\"Design\",\"level\":\"Intro/101\",\"color\":\"#D35D5D\"},{\"id\":2950,\"title\":\"Lets experience Python !\",\"description\":\"Python has been gaining popularity over other scripting languages. In this session I will try to make you guys a bit familiar and more comfortable to this &#8216;battery-charged&#8217; programming language. What can you expect from this session : -Introduction -Basics -Few useful modules -Its Application Note :: To be good in python, you should just [&hellip;] <a href=\\\"http://barcampbangalore.org/bcb/bcb14/lets-experience-python\\\">Read more</a>\",\"permalink\":\"http://barcampbangalore.org/bcb/bcb14/lets-experience-python\",\"time\":\"4:30PM - 5:15PM\",\"location\":\"Battleship\",\"presenter\":\"Adil Imroz\",\"photo\":\"http://graph.facebook.com/100000249718194/picture?type=square\",\"category\":\"Design\",\"level\":\"Intro/101\",\"color\":\"#D35D5D\"},{\"id\":2812,\"title\":\"Ember.js: A framework for creating ambitious web applications\",\"description\":\"Ember.js is designed to help developers build ambitiously large web applications that are competitive with native apps. This is an introductory session to Ember.js with a simple demo application. Why client side MVC framework (Ember.js, Angular.js) over server side rendered HTML? When should you use Ember.js &amp; when should you not? How fast is client side [&hellip;] <a href=\\\"http://barcampbangalore.org/bcb/bcb14/ember-js-a-framework-for-creating-ambitious-web-applications\\\">Read more</a>\",\"permalink\":\"http://barcampbangalore.org/bcb/bcb14/ember-js-a-framework-for-creating-ambitious-web-applications\",\"time\":\"4:30PM - 5:15PM\",\"location\":\"Contra\",\"presenter\":\"gauthamns\",\"photo\":\"http://1.gravatar.com/avatar/9e6def4895a9697d59e65dcde9f26349?s=96&amp;d=wavatar&amp;r=G\",\"category\":\"Design\",\"level\":\"Intro/101\",\"color\":\"#D35D5D\"},{\"id\":2846,\"title\":\"All about playing the Guitar\",\"description\":\"Why one should play guitar? How to choose or buy a right one for you? Software tools that can help with the learning Offline and online guitar learning resources More interactive, demo and no PPT session. <a href=\\\"http://barcampbangalore.org/bcb/bcb14/all-about-playing-the-guitar\\\">Read more</a>\",\"permalink\":\"http://barcampbangalore.org/bcb/bcb14/all-about-playing-the-guitar\",\"time\":\"4:30PM - 5:15PM\",\"location\":\"Diablo\",\"presenter\":\"prathap\",\"photo\":\"http://0.gravatar.com/avatar/81bac959fcacec4ba50267af482558e5?s=96&amp;d=wavatar&amp;r=G\",\"category\":\"Design\",\"level\":\"Intro/101\",\"color\":\"#D35D5D\"},{\"id\":2952,\"title\":\"Smartphone In India: Durby Of Two OSs\",\"description\":\"India is the second largest mobile market by subscribers and third by handset units. The market, though, is largely dominated by Google Android and Apple is making every effort possible to eat up Samsung&#8217;s share, are Indian App developers really making money from Android? Or, its time to shift gear to grow towards iOS? Be [&hellip;] <a href=\\\"http://barcampbangalore.org/bcb/bcb14/smartphone-in-india-durby-of-two-os\\\">Read more</a>\",\"permalink\":\"http://barcampbangalore.org/bcb/bcb14/smartphone-in-india-durby-of-two-os\",\"time\":\"4:30PM - 5:15PM\",\"location\":\"Everquest\",\"presenter\":\"Amit Misra\",\"photo\":\"http://graph.facebook.com/1239473836/picture?type=square\",\"category\":\"Design\",\"level\":\"Intro/101\",\"color\":\"#D35D5D\"},{\"id\":2792,\"title\":\"Introduction to Perl Programming\",\"description\":\"Introduction Basic Syntax When to use Perl? Perl Open source community <a href=\\\"http://barcampbangalore.org/bcb/bcb14/introduction-to-perl-programming\\\">Read more</a>\",\"permalink\":\"http://barcampbangalore.org/bcb/bcb14/introduction-to-perl-programming\",\"time\":\"4:30PM - 5:15PM\",\"location\":\"Fable\",\"presenter\":\"perlsaran\",\"photo\":\"http://0.gravatar.com/avatar/6ccdc9d77411ed92f73539dc256dc17e?s=96&amp;d=wavatar&amp;r=G\",\"category\":\"Design\",\"level\":\"Intro/101\",\"color\":\"#D35D5D\"}]},{\"type\":\"fixed\",\"startTime\":\"1730\",\"endTime\":\"1815\",\"name\":\"Feedback\",\"id\":11}]}";

			if (page.length() > MAX_LOG) {
				int iCount = 0;
				for (; iCount < page.length() - MAX_LOG; iCount += MAX_LOG) {
					Log.e("bcbdata", page.substring(iCount, iCount + MAX_LOG));
				}
				Log.e("bcbdata", page.substring(iCount));
			} else {
				Log.e("bcbdata", page);
			}

			BarcampData data = DataProcessingUtils.parseBCBJSON(page);
			if (data != null) {
				((BarcampBangalore) context).setBarcampData(data);
				retVal = true;
				BCBSharedPrefUtils.setAllBCBUpdates(context, page);
			}

		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if (!retVal) {
			updateContextDataFromSharedPreferences(context);
		}
		return retVal;
	}

	public static void updateContextDataFromSharedPreferences(Context context) {
		try {
			String page = BCBSharedPrefUtils.getAllBCBUpdates(context, null);
			if (page != null) {
				BarcampData data;
				data = DataProcessingUtils.parseBCBJSON(page);
				((BarcampBangalore) context).setBarcampData(data);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static void syncUserScheduleData(Context context) {
		String userID = BCBSharedPrefUtils.getUserID(context);
		String userKey = BCBSharedPrefUtils.getUserKey(context);
		if (TextUtils.isEmpty(userKey) || TextUtils.isEmpty(userID)) {
			return;
		}

		BufferedReader in = null;
		try {
			String userScheduleURL = String.format(BCB_USER_SCHEDULE_URL,
					userID, userKey);
			URL url = new URL(userScheduleURL);
			Log.e("UserURL", userScheduleURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000 /* milliseconds */);
			conn.setConnectTimeout(15000 /* milliseconds */);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			// Starts the query
			conn.connect();
			int response = conn.getResponseCode();
//						Log.d(DEBUG_TAG, "The response is: " + response);
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));


//			HttpClient client = new DefaultHttpClient();
//			String userScheduleURL = String.format(BCB_USER_SCHEDULE_URL,
//					userID, userKey);
//			Log.e("UserURL", userScheduleURL);
//			HttpUriRequest request = new HttpGet(userScheduleURL);
//			HttpResponse response = client.execute(request);
//			in = new BufferedReader(new InputStreamReader(response.getEntity()
//					.getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			in.close();
			String page = sb.toString();
			if (page.length() > MAX_LOG) {
				int iCount = 0;
				for (; iCount < page.length() - MAX_LOG; iCount += MAX_LOG) {
					Log.e("schedule", page.substring(iCount, iCount + MAX_LOG));
				}
				Log.e("schedule", page.substring(iCount));
			} else {
				Log.e("schedule", page);
			}
			List<BarcampUserScheduleData> data = DataProcessingUtils
					.parseBCBScheduleJSON(page);
			((BarcampBangalore) context).setUserSchedule(data);

		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void removeSessionFromSchedule(Context context,
			String sessionid, int slotPos, int sessionPos) {
		BCBSharedPrefUtils.setAlarmSettingsForID(context, sessionid,
				BCBSharedPrefUtils.ALARM_NOT_SET);
		PendingIntent intent = BCBUtils.createPendingIntentForID(context,
				sessionid, slotPos, sessionPos);
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(intent);
	}

	public static void setAlarmForSession(Context context, Slot slot,
			Session session, int slotpos, int sessionpos) {
		BCBSharedPrefUtils.setAlarmSettingsForID(context, session.id,
				BCBSharedPrefUtils.ALARM_SET);
		PendingIntent intent = BCBUtils.createPendingIntentForID(context,
				session.id, slotpos, sessionpos);
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		int hour = slot.startTime / 100;
		int mins = slot.startTime % 100;
		Log.e("Session", "hour : " + hour + " mins :" + mins);
		GregorianCalendar date = new GregorianCalendar(2015, Calendar.NOVEMBER,
				1, hour, mins);
		long timeInMills = date.getTimeInMillis() - 300000;
		alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMills, intent);
	}
}
