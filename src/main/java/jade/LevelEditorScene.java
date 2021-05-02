package jade;

import java.awt.event.KeyEvent;

public class LevelEditorScene extends Scene{

    private boolean changingScene = false;
    private float timeToChangeScene = 2.0f;

    public LevelEditorScene () {
        System.out.println("Inside a Level Editor scene");
    }

    @Override
    public void update(float dt) {
        System.out.println("FPS : " + (1.0f / dt));
        if (!changingScene && KeyListener.isKeyPressed(KeyEvent.VK_SPACE)) {
            changingScene = true;
        }
        if (changingScene && timeToChangeScene > 0) {
            timeToChangeScene -= dt;
            Window.get().r -= dt;
            Window.get().g -= dt;
            Window.get().b -= dt;
        } else if (changingScene) {
            Window.changeScene(1);
        }
    }
}
