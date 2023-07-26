package com.ksyun.trade.constant;


public enum VerifyCodeScene {

    GET_SC_PATH("gsp"), COMMON("c");

    public final String scene;

    VerifyCodeScene(String scene) {
        this.scene = scene;
    }

    public static VerifyCodeScene of(final String scene) {
        VerifyCodeScene[] statuses = values();
        for (VerifyCodeScene VerifyCodeScene : statuses) {
            if (VerifyCodeScene.scene.equals(scene)) {
                return VerifyCodeScene;
            }
        }
        throw new IllegalArgumentException("不合法的VerifyCodeScene:" + scene);
    }
}
