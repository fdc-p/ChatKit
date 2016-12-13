package com.stfalcon.chatkit.sample;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.security.SecureRandom;
import java.util.Date;

/*
 * Created by troy379 on 12.12.16.
 */
public final class Demo {
    private Demo() {
        throw new AssertionError();
    }

    private static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static SecureRandom rnd = new SecureRandom();

    public static String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    private static int getRandomInt() {
        return rnd.nextInt(100) + 2;
    }

    public static class Message implements IMessage {

        private int id;
        private String text;

        public Message(int id) {
            this.id = id;
        }

        public Message(int id, String text) {
            this.id = id;
            this.text = text;
        }

        @Override
        public Date getCreatedAt() {
            return new Date();
        }

        @Override
        public String getId() {
            return Integer.toString(id);
        }

        @Override
        public IUser getUser() {
            return new IUser() {
                @Override
                public String getId() {
                    return id % 2 == 0 ? "0" : "1";
                }

                @Override
                public String getName() {
                    return "Peter";
                }

                @Override
                public String getAvatar() {
                    return id % 2 == 0
                            ? "https://pickaface.net/assets/images/slides/slide2.png"
                            : "https://acadeu.com/static/img/demo/avatars/jorgediaz.jpg";
                }
            };
        }

        @Override
        public String getText() {
            return randomString(getRandomInt());
        }

        @Override
        public String getStatus() {
            return "Sent";
        }
    }
}