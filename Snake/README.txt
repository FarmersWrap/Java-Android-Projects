      Zhenglin Yu 
      openjdk version "11.0.8" 2020-10-14
      macOS 10.15.6 (iMac 2020)
    
Game rules:
Arrow keys: turn the snake in the direction indicated
P: pause and un-pause the game
R: reset to the splash screen
1: start level 1
2: start level 2
3: start level 3
Q: quit and display the high score screen

The game starts with a splash screen that includes your name, userid, and a description
 of how to play the game. The game should wait here until the user presses the appropriate
 key to start the game, and then it should transition to the second screen.
The second screen represents "level 1" of the game, with a snake and 5 pieces of fruit on 
the screen in a pattern of your choice (not random, it should be the same every time you 
run the game). As the snake eats one fruit, another fruit instantly appears at a random
 point on the screen to replace the eaten fruit. A timer at the top of the screen counts-down 
from 30. When it reaches zero, the player "wins" this level, and moves to the next level.
The third screen represents "level 2" of the game, with a snake and 10 pieces of fruit on the
 screen in a pattern of your choice (not random, it should be the same every time you run the 
game). The snake should be faster than the previous level. As the snake eats one fruit, another 
fruit instantly appears at a random point on the screen to replace the eaten fruit. A timer at 
the top of the screen counts-down from 30. When it reaches zero, the player "wins" this level, 
and moves to the next level.
The fourth screen represents "level 3" of the game, with a snake and 15 pieces of fruit on the 
screen in a pattern of your choice (not random, it should be the same every time you run the game). 
The snake should be faster than the previous level (i.e. fast). As the snake eats one fruit, 
another fruit instantly appears at a random point on the screen to replace the eaten fruit. 
This level does not have a timer, and runs until the player dies.
When the player dies, the game is "over". Display a final screen informing them that the game 
is over, with their high score.




Notations:
1. Fruit may be generated under the snake. It makes sense that a fruit can 
grow if the snake does not eat it. When you are using key "1" or key "2" or
key "3" to change the level of the game; if the snake occupies the "fixed" 
initial positions, the fruits will be generated under the body of the snake.

2. Our resolution is 1260 x 780

3. 3 levels are represented by different fruits, we will count the number of
each fruit in each level. The score is calculated by the sum of the fruits.

4. Only left and right keys are available for gamers, I have a version using 
4 keys to control the snake and some advanced features to help smoothly turn
direction. Please send me an email if you want to play it.


