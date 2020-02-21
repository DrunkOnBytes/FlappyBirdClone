package com.arjunsinha.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	ShapeRenderer shapeRenderer;

	Texture[] birds;
	Texture up;
	Texture down;
	Texture gameover;
	int flapState = 0;

	float birdY = 0;
	float velocity = 0;

	int gameState = 0;
	float gravity = 1.5f;
	float gap = 400;

	float maxTubeOffset;
	Random random;
	float tubeVelocity = 4;

	int noOfTubes = 4;
	float[] tubeX = new float[noOfTubes];
	float[] tubeOffset = new float[noOfTubes];
	float distanceBetweenTubes;

	Circle birdCircle;
	Rectangle[] upTubesRect= new Rectangle[noOfTubes];
	Rectangle[] downTubesRect= new Rectangle[noOfTubes];

	int score;
	int scoringTube;

	BitmapFont font;

	@Override
	public void create() {
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		birdCircle = new Circle();

		font=new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);//font size

        for(int i=0;i<noOfTubes;i++) {
			upTubesRect[i] = new Rectangle();
			downTubesRect[i] = new Rectangle();
		}

		background = new Texture("bg.png");
		birds = new Texture[2];
		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird2.png");
		up = new Texture("toptube.png");
		down = new Texture("bottomtube.png");
		gameover= new Texture("gameover.png");

        maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 100;
        random = new Random();
        distanceBetweenTubes = Gdx.graphics.getWidth() * 3 / 4;

        startGame();

	}

	public void startGame(){

        birdY = Gdx.graphics.getHeight() / 2 - birds[0].getHeight() / 2;
        score=0;
        scoringTube=0;
        velocity=0;

        for (int i = 0; i < noOfTubes; i++) {

            tubeOffset[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
            tubeX[i] = Gdx.graphics.getWidth() / 2 - up.getWidth() / 2 + ((i + 1) * distanceBetweenTubes);
        }
    }

	@Override
	public void render() {

		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());//set background
		//shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		//shapeRenderer.setColor(Color.RED);

		if (gameState ==1) {


			if (Gdx.input.justTouched()) {

				Gdx.app.log("Touched", "Yep!");
				velocity = -25;

			}

			for (int i = 0; i < noOfTubes; i++) {

				if (tubeX[i] < -up.getWidth()) {

					tubeX[i] += noOfTubes * distanceBetweenTubes;
					tubeOffset[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);

				} else {

					tubeX[i] -= tubeVelocity;

				}

				batch.draw(up, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
				batch.draw(down, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - down.getHeight() + tubeOffset[i]);

				upTubesRect[i]=new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], up.getWidth(), up.getHeight());
				downTubesRect[i]=new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - down.getHeight() + tubeOffset[i], up.getWidth(), up.getHeight());
			}

			if (birdY > 0 ) {
				velocity += gravity;//Increases velocity of fall
				birdY -= velocity;//makes the bird fall
			}
			else{
				gameState=2;
			}

			batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2 - birds[flapState].getWidth() / 2, birdY);//x,y is coordinate of bottom left of sprite
			flapState = (flapState + 1) % 2;

			if(tubeX[scoringTube] < Gdx.graphics.getWidth()/2 ){ //Scoring

				score++;
				scoringTube=(scoringTube+1)%noOfTubes;
			}

			for(int i=0 ; i<noOfTubes ; i++) {  //Detect Collision
				if (Intersector.overlaps(birdCircle, upTubesRect[i]) || Intersector.overlaps(birdCircle, downTubesRect[i])) {

					Gdx.app.log("Collision, Score", Integer.toString(score));
					gameState = 2;
				}
			}

		}
		else if(gameState==0){

			batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2 - birds[flapState].getWidth() / 2, birdY);//x,y is coordinate of bottom left of sprite
			flapState = (flapState + 1) % 2;
			if (Gdx.input.justTouched()) {

				Gdx.app.log("Touched", "Begin");
				gameState = 1;

			}

		}
		else{

			batch.draw(birds[0], Gdx.graphics.getWidth() / 2 - birds[flapState].getWidth() / 2, Gdx.graphics.getHeight()/2 - 300);

			batch.draw(gameover, Gdx.graphics.getWidth()/2 - gameover.getWidth()/2, Gdx.graphics.getHeight()/2 - gameover.getHeight()/2);

			if (Gdx.input.justTouched()) {

				Gdx.app.log("Touched", "Restart");
				gameState = 1;
				startGame();

			}
		}



		font.draw(batch, String.valueOf(score), 100, 200);

		batch.end();

		birdCircle.set(Gdx.graphics.getWidth()/2, birdY + birds[flapState].getHeight()/2, birds[flapState].getWidth()/2-5);



	}
}
