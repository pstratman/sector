package com.security.sector;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class US23SmokeTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void uS23SmokeTest() {
        ViewInteraction appCompatTextView = onView(
                allOf(withId(R.id.list_item_label), withText("API Demos"),
                        childAtPosition(
                                withId(R.id.list_view),
                                0),
                        isDisplayed()));
        appCompatTextView.perform(click());

        ViewInteraction appCompatTextView2 = onView(
                allOf(withId(android.R.id.title), withText("Requested Resources"), isDisplayed()));
        appCompatTextView2.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.requestedResourcesContent), withText("android.permission.READ_CONTACTS\nandroid.permission.WRITE_CONTACTS\nandroid.permission.VIBRATE\nandroid.permission.ACCESS_COARSE_LOCATION\nandroid.permission.INTERNET\nandroid.permission.SET_WALLPAPER\nandroid.permission.WRITE_EXTERNAL_STORAGE\nandroid.permission.SEND_SMS\nandroid.permission.RECEIVE_SMS\nandroid.permission.RECEIVE_MMS\nandroid.permission.WRITE_SMS\nandroid.permission.READ_SMS\nandroid.permission.NFC\nandroid.permission.TRANSMIT_IR\nandroid.permission.READ_PHONE_STATE\nandroid.permission.WAKE_LOCK\nandroid.permission.RECORD_AUDIO\nandroid.permission.CAMERA\nandroid.permission.READ_EXTERNAL_STORAGE\n"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.TableLayout.class),
                                        1),
                                0),
                        isDisplayed()));
        textView.check(matches(isDisplayed()));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.otherResourcesContent), withText("No other permissions listed in application manifest."),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.TableLayout.class),
                                        3),
                                0),
                        isDisplayed()));
        textView2.check(matches(isDisplayed()));

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
