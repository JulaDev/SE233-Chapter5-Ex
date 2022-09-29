package se233.Chapter5;
import javafx.embed.swing.JFXPanel;
import javafx.scene.input.KeyCode;
import org.junit.Before;
import org.junit.Test;
import se233.Chapter5.Controller.Drawingloop;
import se233.Chapter5.Controller.Gameloop;
import se233.Chapter5.model.Character;
import se233.Chapter5.view.Platform;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class CharacterTest {
    private Character floatingCharacter;
    private ArrayList<Character> characterListUnderTest;
    private Platform platformUnderTest;
    private Gameloop gameLoopUnderTest;
    private Drawingloop drawingLoopUnderTest;
    private Method updateMethod, redrawMethod;
    private Character groundCharacter;
    private Character borderCharacter;
    private Character getCollidedCharacter;
    private Character nextToGroundCharacter;
    @Before
    public void setup() {
        JFXPanel jfxPanel = new JFXPanel();
        floatingCharacter = new Character(30, 30, 0, 0, KeyCode.A, KeyCode.D, KeyCode.W);
        groundCharacter = new Character(30, Platform.GROUND-Character.CHARACTER_HEIGHT-1,0,0, KeyCode.LEFT,KeyCode.RIGHT,KeyCode.UP);
        borderCharacter = new Character(-1,Platform.GROUND-Character.CHARACTER_HEIGHT-1,0,0,KeyCode.LEFT,KeyCode.RIGHT,KeyCode.UP);
        getCollidedCharacter = new Character(30,31,0,0,KeyCode.LEFT,KeyCode.RIGHT,KeyCode.UP);
        nextToGroundCharacter = new Character(30 + Character.CHARACTER_WIDTH + 1,Platform.GROUND-Character.CHARACTER_HEIGHT-1,0,0,KeyCode.A,KeyCode.D,KeyCode.W);
        characterListUnderTest = new ArrayList<Character>();
        characterListUnderTest.add(floatingCharacter);
        characterListUnderTest.add(groundCharacter);
        characterListUnderTest.add(borderCharacter);
        characterListUnderTest.add(getCollidedCharacter);
        characterListUnderTest.add(nextToGroundCharacter);
        platformUnderTest = new Platform();
        gameLoopUnderTest = new Gameloop(platformUnderTest);
        drawingLoopUnderTest = new Drawingloop(platformUnderTest);
        try {
            updateMethod = Gameloop.class.getDeclaredMethod("update", ArrayList.class);
            redrawMethod = Drawingloop.class.getDeclaredMethod("paint", ArrayList.class);
            updateMethod.setAccessible(true);
            redrawMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            updateMethod = null;
            redrawMethod = null;
        }
    }

    @Test
    public void characterInitialValuesShouldMatchConstructorArguments() {
        assertEquals("Initial x", 30, floatingCharacter.getX(), 0);
        assertEquals("Initial y", 30, floatingCharacter.getY(), 0);
        assertEquals("Offset x", 0, floatingCharacter.getOffsetX(), 0.0);
        assertEquals("Offset y", 0, floatingCharacter.getOffsetY(), 0.0);
        assertEquals("Left key", KeyCode.A, floatingCharacter.getLeftKey());
        assertEquals("Right key", KeyCode.D, floatingCharacter.getRightKey());
        assertEquals("Up key", KeyCode.W, floatingCharacter.getUpKey());
    }

    @Test
    public void characterShouldMoveToTheLeftAfterTheLeftKeyIsPressed() throws IllegalAccessException, InvocationTargetException, NoSuchFieldException {
        Character characterUnderTest = characterListUnderTest.get(0);
        int startX = characterUnderTest.getX();
        platformUnderTest.getKeys().add(KeyCode.A);
        updateMethod.invoke(gameLoopUnderTest, characterListUnderTest);
        redrawMethod.invoke(drawingLoopUnderTest, characterListUnderTest);
        Field isMoveLeft = characterUnderTest.getClass().getDeclaredField("isMoveLeft");
        isMoveLeft.setAccessible(true);
        assertTrue("Controller:Left key pressing is acknowledged", platformUnderTest.getKeys().isPressed(KeyCode.A));
        assertTrue("Model:Character moving left state is set", isMoveLeft.getBoolean(characterUnderTest));
        assertTrue("View:Character is moving left", characterUnderTest.getX() < startX);
    }

    @Test
    public void characterShouldMoveToTheRightWithRightSpeedAfterTheRightKeyIsPressed() throws IllegalAccessException, InvocationTargetException, NoSuchFieldException {
        Character characterUnderTest = characterListUnderTest.get(0);
        int startX = characterUnderTest.getX();
        platformUnderTest.getKeys().add(KeyCode.D);
        updateMethod.invoke(gameLoopUnderTest, characterListUnderTest);
        redrawMethod.invoke(drawingLoopUnderTest, characterListUnderTest);
        Field isMoveRight = characterUnderTest.getClass().getDeclaredField("isMoveRight");
        isMoveRight.setAccessible(true);
        assertTrue("Controller:Right key pressing is acknowledged", platformUnderTest.getKeys().isPressed(KeyCode.D));
        assertTrue("Model:Character moving right state is set", isMoveRight.getBoolean(characterUnderTest));
        assertTrue("View:Character is moving right", characterUnderTest.getX() > startX);
        //right speed
        assertTrue(characterUnderTest.getxVelocity() == 1);

    }

    @Test
    public void characterShouldJumpWhenOnTheGround() throws IllegalAccessException, InvocationTargetException, NoSuchFieldException {
        Character characterUnderTest = characterListUnderTest.get(0);
        platformUnderTest.getKeys().add(KeyCode.W);
        updateMethod.invoke(gameLoopUnderTest, characterListUnderTest);
        redrawMethod.invoke(drawingLoopUnderTest, characterListUnderTest);
        characterUnderTest.checkReachFloor();
        Field isJumping = characterUnderTest.getClass().getDeclaredField("isJumping");
        Field isFalling = characterUnderTest.getClass().getDeclaredField("isFalling");
        isFalling.setAccessible(true);
        isJumping.setAccessible(true);
        assertFalse("character can jump", characterUnderTest.isCanJump());
        assertFalse("character is jump", characterUnderTest.isJumping());
        assertTrue("character is Falling", characterUnderTest.isFalling());

    }
    @Test
    public void characterIsJumpWhenCharacterNotOnTheGround() throws IllegalAccessException, InvocationTargetException, NoSuchFieldException {

        Character characterUnderTest = characterListUnderTest.get(0);
        int startY = characterUnderTest.getY();
        platformUnderTest.getKeys().add(KeyCode.W);
        updateMethod.invoke(gameLoopUnderTest,characterListUnderTest);
        redrawMethod.invoke(drawingLoopUnderTest, characterListUnderTest);
        Field isJumping = characterUnderTest.getClass().getDeclaredField("isJumping");
        Field isFalling = characterUnderTest.getClass().getDeclaredField("isFalling");
        isJumping.setAccessible(true);
        isFalling.setAccessible(true);
        assertTrue("Up Button is pressed",platformUnderTest.getKeys().isPressed(KeyCode.W));
        assertTrue("isFalling is True", isFalling.getBoolean(characterUnderTest));
        assertFalse("isJumping is False", isJumping.getBoolean(characterUnderTest));
        assertTrue("Character is Falling", characterUnderTest.getY() > startY);
    }
    @Test
    public void CharacterShouldStopAfterAnyAttempTowardIt() throws IllegalAccessException, InvocationTargetException, NoSuchFieldException {
        Character characterUnderTest = characterListUnderTest.get(0);
        int startX = characterUnderTest.getX();
        redrawMethod.invoke(drawingLoopUnderTest, characterListUnderTest);
        characterUnderTest.checkReachGameWall();
        platformUnderTest.getKeys().add(KeyCode.LEFT);
        updateMethod.invoke(gameLoopUnderTest,characterListUnderTest);
        redrawMethod.invoke(drawingLoopUnderTest,characterListUnderTest);
        Field isMoveLeft = characterUnderTest.getClass().getDeclaredField("isMoveLeft");
        isMoveLeft.setAccessible(true);
        assertEquals("Character should stop at the border",startX, characterUnderTest.getX());

    }
    @Test
    public void consequenceAfterCharacterHasCollidedAnotherCharacter() throws InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        Character characterA =borderCharacter;
        Character characterB =groundCharacter;
        platformUnderTest.getKeys().add(KeyCode.A);
        int startX = characterB.getX();
        updateMethod.invoke(gameLoopUnderTest,characterListUnderTest);
        redrawMethod.invoke(drawingLoopUnderTest,characterListUnderTest);
        characterB.collided(characterA);
        characterA.collided(characterB);
        Field isMoveLeft = characterB.getClass().getDeclaredField("isMoveLeft");
        isMoveLeft.setAccessible(true);
        assertTrue("Left key pressing is acknowledge", platformUnderTest.getKeys().isPressed(KeyCode.A));
        assertEquals("CharacterB should not move",startX,characterB.getX());
    }
    @Test
    public void characterHasStompsOtherCharacter() throws InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        Character characterUpperOne = characterListUnderTest.get(0);
        Character characterLowerOne = characterListUnderTest.get(3);
        int startScore = characterUpperOne.getScore();
        int startYofTheLowerOne = characterLowerOne.getY();
        redrawMethod.invoke(drawingLoopUnderTest, characterListUnderTest);
        characterUpperOne.collided(characterLowerOne);
        updateMethod.invoke(gameLoopUnderTest,characterListUnderTest);
        assertTrue("Score is increase", characterUpperOne.getScore() > startScore);
        assertEquals("characterLowerOne will respawn at the same place" , characterLowerOne.getY(), startYofTheLowerOne);
    }

}