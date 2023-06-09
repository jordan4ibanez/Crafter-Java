package engine.gui;

import engine.graphics.Mesh;
import engine.graphics.Texture;

final public class TextHandling {

    //textures
    private final Texture fontTextureAtlas = new Texture("textures/font.png");


    //yes I know, I could use a TTF font, but where's the fun in that?
    public float[] translateCharToArray(char thisChar){

        float[] letterArray = new float[]{0,0,0};

        float FONT_PIXEL_WIDTH = 6f;
        switch (thisChar) {
            case 'a' -> {
                letterArray[0] = 0;
                letterArray[1] = 1;
                letterArray[2] = 4f / FONT_PIXEL_WIDTH;
            }
            case 'A' -> {
                letterArray[0] = 0;
                letterArray[1] = 0;
                letterArray[2] = 1f;
            }
            case 'b' -> {
                letterArray[0] = 1;
                letterArray[1] = 1;
                letterArray[2] = 4f / FONT_PIXEL_WIDTH;
            }
            case 'B' -> {
                letterArray[0] = 1;
                letterArray[1] = 0;
                letterArray[2] = 1f;
            }
            case 'c' -> {
                letterArray[0] = 2;
                letterArray[1] = 1;
                letterArray[2] = 3f / FONT_PIXEL_WIDTH;
            }
            case 'C' -> {
                letterArray[0] = 2;
                letterArray[1] = 0;
                letterArray[2] = 1f;
            }
            case 'd' -> {
                letterArray[0] = 3;
                letterArray[1] = 1;
                letterArray[2] = 4f / FONT_PIXEL_WIDTH;
            }
            case 'D' -> {
                letterArray[0] = 3;
                letterArray[1] = 0;
                letterArray[2] = 1f;
            }
            case 'e' -> {
                letterArray[0] = 4;
                letterArray[1] = 1;
                letterArray[2] = 4f / FONT_PIXEL_WIDTH;
            }
            case 'E' -> {
                letterArray[0] = 4;
                letterArray[1] = 0;
                letterArray[2] = 1f;
            }
            case 'f' -> {
                letterArray[0] = 5;
                letterArray[1] = 1;
                letterArray[2] = 4f / FONT_PIXEL_WIDTH;
            }
            case 'F' -> {
                letterArray[0] = 5;
                letterArray[1] = 0;
                letterArray[2] = 1f;
            }
            case 'g' -> {
                letterArray[0] = 6;
                letterArray[1] = 1;
                letterArray[2] = 4f / FONT_PIXEL_WIDTH;
            }
            case 'G' -> {
                letterArray[0] = 6;
                letterArray[1] = 0;
                letterArray[2] = 1f;
            }
            case 'h' -> {
                letterArray[0] = 7;
                letterArray[1] = 1;
                letterArray[2] = 4f / FONT_PIXEL_WIDTH;
            }
            case 'H' -> {
                letterArray[0] = 7;
                letterArray[1] = 0;
                letterArray[2] = 1f;
            }
            case 'i' -> {
                letterArray[0] = 8;
                letterArray[1] = 1;
                letterArray[2] = 1f / FONT_PIXEL_WIDTH;
            }
            case 'I' -> {
                letterArray[0] = 8;
                letterArray[1] = 0;
                letterArray[2] = 1f;
            }
            case 'j' -> {
                letterArray[0] = 9;
                letterArray[1] = 1;
                letterArray[2] = 3f / FONT_PIXEL_WIDTH;
            }
            case 'J' -> {
                letterArray[0] = 9;
                letterArray[1] = 0;
                letterArray[2] = 1f;
            }
            case 'k' -> {
                letterArray[0] = 10;
                letterArray[1] = 1;
                letterArray[2] = 4f / FONT_PIXEL_WIDTH;
            }
            case 'K' -> {
                letterArray[0] = 10;
                letterArray[1] = 0;
                letterArray[2] = 1f;
            }
            case 'l' -> {
                letterArray[0] = 11;
                letterArray[1] = 1;
                letterArray[2] = 1f / FONT_PIXEL_WIDTH;
            }
            case 'L' -> {
                letterArray[0] = 11;
                letterArray[1] = 0;
                letterArray[2] = 1f;
            }
            case 'm' -> {
                letterArray[0] = 12;
                letterArray[1] = 1;
                letterArray[2] = 1f;
            }
            case 'M' -> {
                letterArray[0] = 12;
                letterArray[1] = 0;
                letterArray[2] = 1f;
            }
            case 'n' -> {
                letterArray[0] = 13;
                letterArray[1] = 1;
                letterArray[2] = 4f / FONT_PIXEL_WIDTH;
            }
            case 'N' -> {
                letterArray[0] = 13;
                letterArray[1] = 0;
                letterArray[2] = 1f;
            }
            case 'o' -> {
                letterArray[0] = 14;
                letterArray[1] = 1;
                letterArray[2] = 4f / FONT_PIXEL_WIDTH;
            }
            case 'O' -> {
                letterArray[0] = 14;
                letterArray[1] = 0;
                letterArray[2] = 1f;
            }
            case 'p' -> {
                letterArray[0] = 15;
                letterArray[1] = 1;
                letterArray[2] = 3f / FONT_PIXEL_WIDTH;
            }
            case 'P' -> {
                letterArray[0] = 15;
                letterArray[1] = 0;
                letterArray[2] = 1f;
            }
            case 'q' -> {
                letterArray[0] = 16;
                letterArray[1] = 1;
                letterArray[2] = 3f / FONT_PIXEL_WIDTH;
            }
            case 'Q' -> {
                letterArray[0] = 16;
                letterArray[1] = 0;
                letterArray[2] = 1f;
            }
            case 'r' -> {
                letterArray[0] = 17;
                letterArray[1] = 1;
                letterArray[2] = 4f / FONT_PIXEL_WIDTH;
            }
            case 'R' -> {
                letterArray[0] = 17;
                letterArray[1] = 0;
                letterArray[2] = 1f;
            }
            case 's' -> {
                letterArray[0] = 18;
                letterArray[1] = 1;
                letterArray[2] = 4f / FONT_PIXEL_WIDTH;
            }
            case 'S' -> {
                letterArray[0] = 18;
                letterArray[1] = 0;
                letterArray[2] = 1f;
            }
            case 't' -> {
                letterArray[0] = 19;
                letterArray[1] = 1;
                letterArray[2] = 4f / FONT_PIXEL_WIDTH;
            }
            case 'T' -> {
                letterArray[0] = 19;
                letterArray[1] = 0;
                letterArray[2] = 1f;
            }
            case 'u' -> {
                letterArray[0] = 20;
                letterArray[1] = 1;
                letterArray[2] = 4f / FONT_PIXEL_WIDTH;
            }
            case 'U' -> {
                letterArray[0] = 20;
                letterArray[1] = 0;
                letterArray[2] = 1f;
            }
            case 'v' -> {
                letterArray[0] = 21;
                letterArray[1] = 1;
                letterArray[2] = 1f;
            }
            case 'V' -> {
                letterArray[0] = 21;
                letterArray[1] = 0;
                letterArray[2] = 1f;
            }
            case 'w' -> {
                letterArray[0] = 22;
                letterArray[1] = 1;
                letterArray[2] = 1f;
            }
            case 'W' -> {
                letterArray[0] = 22;
                letterArray[1] = 0;
                letterArray[2] = 1f;
            }
            case 'x' -> {
                letterArray[0] = 23;
                letterArray[1] = 1;
                letterArray[2] = 1f;
            }
            case 'X' -> {
                letterArray[0] = 23;
                letterArray[1] = 0;
                letterArray[2] = 1f;
            }
            case 'y' -> {
                letterArray[0] = 24;
                letterArray[1] = 1;
                letterArray[2] = 4f / FONT_PIXEL_WIDTH;
            }
            case 'Y' -> {
                letterArray[0] = 24;
                letterArray[1] = 0;
                letterArray[2] = 1f;
            }
            case 'z' -> {
                letterArray[0] = 25;
                letterArray[1] = 1;
                letterArray[2] = 1f;
            }
            case 'Z' -> {
                letterArray[0] = 25;
                letterArray[1] = 0;
                letterArray[2] = 1f;
            }
            //now I know my ABCs

            case '0' -> {
                letterArray[0] = 26;
                letterArray[1] = 0;
                letterArray[2] = 1f;
            }
            case '1' -> {
                letterArray[0] = 27;
                letterArray[1] = 0;
                letterArray[2] = 1f;
            }
            case '2' -> {
                letterArray[0] = 28;
                letterArray[1] = 0;
                letterArray[2] = 1f;
            }
            case '3' -> {
                letterArray[0] = 29;
                letterArray[1] = 0;
                letterArray[2] = 1f;
            }
            case '4' -> {
                letterArray[0] = 30;
                letterArray[1] = 0;
                letterArray[2] = 1f;
            }
            case '5' -> {
                letterArray[0] = 31;
                letterArray[1] = 0;
                letterArray[2] = 1f;
            }
            case '6' -> {
                letterArray[0] = 32;
                letterArray[1] = 0;
                letterArray[2] = 1f;
            }
            case '7' -> {
                letterArray[0] = 33;
                letterArray[1] = 0;
                letterArray[2] = 1f;
            }
            case '8' -> {
                letterArray[0] = 34;
                letterArray[1] = 0;
                letterArray[2] = 1f;
            }
            case '9' -> {
                letterArray[0] = 35;
                letterArray[1] = 0;
                letterArray[2] = 1f;
            }
            case '.' -> {
                letterArray[0] = 26;
                letterArray[1] = 1;
                letterArray[2] = 1f / FONT_PIXEL_WIDTH;
            }
            case '!' -> {
                letterArray[0] = 27;
                letterArray[1] = 1;
                letterArray[2] = 1f / FONT_PIXEL_WIDTH;
            }
            case '?' -> {
                letterArray[0] = 28;
                letterArray[1] = 1;
                letterArray[2] = 4f / FONT_PIXEL_WIDTH;
            }
            case ' ' -> {
                letterArray[0] = 29;
                letterArray[1] = 1;
                letterArray[2] = 3f / FONT_PIXEL_WIDTH;
            }
            case '-' -> {
                letterArray[0] = 30;
                letterArray[1] = 1;
                letterArray[2] = 1f;
            }
            case ':' -> {
                letterArray[0] = 31;
                letterArray[1] = 1;
                letterArray[2] = 1f / FONT_PIXEL_WIDTH;
            }
            case ',' -> {
                letterArray[0] = 32;
                letterArray[1] = 1;
                letterArray[2] = 1f / FONT_PIXEL_WIDTH;
            }
            case '/' -> {
                letterArray[0] = 33;
                letterArray[1] = 1;
                letterArray[2] = 1f;
            }
            case '\\' -> {
                letterArray[0] = 34;
                letterArray[1] = 1;
                letterArray[2] = 1f;
            }
            case '$' -> {
                letterArray[0] = 35;
                letterArray[1] = 1;
                letterArray[2] = 1f;
            }
            case '@' -> {
                letterArray[0] = 0;
                letterArray[1] = 2;
                letterArray[2] = 1f;
            }
            case '#' -> {
                letterArray[0] = 1;
                letterArray[1] = 2;
                letterArray[2] = 1f;
            }
            case '%' -> {
                letterArray[0] = 2;
                letterArray[1] = 2;
                letterArray[2] = 1f;
            }
            case '^' -> {
                letterArray[0] = 3;
                letterArray[1] = 2;
                letterArray[2] = 1f;
            }
            case '&' -> {
                letterArray[0] = 4;
                letterArray[1] = 2;
                letterArray[2] = 1f;
            }
            case '*' -> {
                letterArray[0] = 5;
                letterArray[1] = 2;
                letterArray[2] = 3f / FONT_PIXEL_WIDTH;
            }
            case '(' -> {
                letterArray[0] = 6;
                letterArray[1] = 2;
                letterArray[2] = 3f / FONT_PIXEL_WIDTH;
            }
            case ')' -> {
                letterArray[0] = 7;
                letterArray[1] = 2;
                letterArray[2] = 3f / FONT_PIXEL_WIDTH;
            }
            case '_' -> {
                letterArray[0] = 8;
                letterArray[1] = 2;
                letterArray[2] = 1f;
            }
            case '=' -> {
                letterArray[0] = 9;
                letterArray[1] = 2;
                letterArray[2] = 1f;
            }
            case '+' -> {
                letterArray[0] = 10;
                letterArray[1] = 2;
                letterArray[2] = 1f;
            }
            case '[' -> {
                letterArray[0] = 11;
                letterArray[1] = 2;
                letterArray[2] = 3f / FONT_PIXEL_WIDTH;
            }
            case ']' -> {
                letterArray[0] = 12;
                letterArray[1] = 2;
                letterArray[2] = 3f / FONT_PIXEL_WIDTH;
            }
            case '|' -> {
                letterArray[0] = 13;
                letterArray[1] = 2;
                letterArray[2] = 1f / FONT_PIXEL_WIDTH;
            }
            case ';' -> {
                letterArray[0] = 14;
                letterArray[1] = 2;
                letterArray[2] = 1f / FONT_PIXEL_WIDTH;
            }
            case '\'' -> {
                letterArray[0] = 15;
                letterArray[1] = 2;
                letterArray[2] = 1f / FONT_PIXEL_WIDTH;
            }
            case '\"' -> {
                letterArray[0] = 16;
                letterArray[1] = 2;
                letterArray[2] = 3f / FONT_PIXEL_WIDTH;
            }
            case '<' -> {
                letterArray[0] = 17;
                letterArray[1] = 2;
                letterArray[2] = 4f / FONT_PIXEL_WIDTH;
            }
            case '>' -> {
                letterArray[0] = 18;
                letterArray[1] = 2;
                letterArray[2] = 4f / FONT_PIXEL_WIDTH;
            }
            case '`' -> {
                letterArray[0] = 19;
                letterArray[1] = 2;
                letterArray[2] = 2f / FONT_PIXEL_WIDTH;
            }
            case '~' -> {
                letterArray[0] = 20;
                letterArray[1] = 2;
                letterArray[2] = 1f;
            }
            case '{' -> {
                letterArray[0] = 21;
                letterArray[1] = 2;
                letterArray[2] = 3f / FONT_PIXEL_WIDTH;
            }
            case '}' -> {
                letterArray[0] = 22;
                letterArray[1] = 2;
                letterArray[2] = 3f / FONT_PIXEL_WIDTH;
            }
            default -> { //all unknown end up as a white box  ¯\_(ツ)_/¯
                letterArray[0] = 23;
                letterArray[1] = 2;
                letterArray[2] = 1f;
            }
        }

        if (letterArray[2] == 1f){
            letterArray[2] = 5f/ FONT_PIXEL_WIDTH;
        }

        float[] returningArray = new float[5];

        //mapping of the texture
        float LETTER_WIDTH = 6f;
        float FONT_WIDTH = 216f;
        returningArray[0] = (letterArray[0] * LETTER_WIDTH) / FONT_WIDTH; //-x

        //fix subtraction of 1 (or keep it for float imprecision problems causing font bleed)

        //returningArray[1] = ((letterArray[0] * LETTER_WIDTH) + LETTER_WIDTH - 1) / FONT_WIDTH; //+x
        returningArray[1] = ((letterArray[0] * LETTER_WIDTH) + (letterArray[2] * LETTER_WIDTH)) / FONT_WIDTH; //+x

        float FONT_HEIGHT = 24f;
        float LETTER_HEIGHT = 8f;
        returningArray[2] = (letterArray[1] * LETTER_HEIGHT) / FONT_HEIGHT; //-y
        returningArray[3] = ((letterArray[1] * LETTER_HEIGHT) + LETTER_HEIGHT - 1) / FONT_HEIGHT; //+y

        //width of the letter (base 1/width), tells the engine how much width to add
        returningArray[4] = letterArray[2];

        return returningArray;
    }


    public Mesh createTextCentered(String text, float r, float g, float b){

        //calculate the length for the entire string
        float totalLengthReal = 0;

        //pre-poll the actual length
        for (char letter : text.toCharArray()) {
            float[] thisCharacterArray = translateCharToArray(letter);
            totalLengthReal += thisCharacterArray[4] + 0.1f;
        }
        totalLengthReal -= 0.1f;

        //x is the actual position in the mesh creation of the letter
        //divide the actual length before it's created, start the typewriter
        //half way across
        float x = -totalLengthReal/2f;

        //get the amount of letters in the string
        int stringLength = text.length();

        float[] positions = new float[stringLength * 12];
        float[] textureCoord = new float[stringLength * 8];
        int[] indices = new int[stringLength * 6];

        float[] light = new float[stringLength * 12];

        int indicesCount = 0;
        int i = 0; //positions count
        int a = 0; //light count
        int w = 0; //textureCoord count
        int t = 0; //indices count

        for (char letter : text.toCharArray()) {

            //translate the character (char primitive) into a usable float array
            float[] thisCharacterArray = translateCharToArray(letter);

            positions[i     ] = (x + thisCharacterArray[4]);
            positions[i + 1 ] = (0.5f);
            positions[i + 2 ] = (0f);

            positions[i + 3 ] = (x);
            positions[i + 4 ] = (0.5f);
            positions[i + 5 ] = (0f);

            positions[i + 6 ] = (x);
            positions[i + 7 ] = (-0.5f);
            positions[i + 8 ] = (0f);

            positions[i + 9 ] = (x + thisCharacterArray[4]);
            positions[i + 10] = (-0.5f);
            positions[i + 11] = (0f);
            i += 12;

            for (int q = 0; q < 4; q++) {
                light[a    ] = (r);
                light[a + 1] = (g);
                light[a + 2] = (b);
                a += 3;
            }

            indices[t    ] = (indicesCount);
            indices[t + 1] = (1 + indicesCount);
            indices[t + 2] = (2 + indicesCount);
            indices[t + 3] = (indicesCount);
            indices[t + 4] = (2 + indicesCount);
            indices[t + 5] = (3 + indicesCount);

            t += 6;
            indicesCount += 4;


            textureCoord[w    ] = (thisCharacterArray[1]);
            textureCoord[w + 1] = (thisCharacterArray[2]);
            textureCoord[w + 2] = (thisCharacterArray[0]);
            textureCoord[w + 3] = (thisCharacterArray[2]);
            textureCoord[w + 4] = (thisCharacterArray[0]);
            textureCoord[w + 5] = (thisCharacterArray[3]);
            textureCoord[w + 6] = (thisCharacterArray[1]);
            textureCoord[w + 7] = (thisCharacterArray[3]);
            w += 8;

            //shift the left of the letter to the right
            //kind of like a type writer

            //make this use the 4th float array variable to space properly
            //add 0.1f so that characters are not squished together

            x += thisCharacterArray[4] + 0.1f;
        }

        return new Mesh(positions, light, indices, textureCoord, fontTextureAtlas);
    }

    public Mesh createTextCenteredWithShadow(String text, float r, float g, float b){

        //calculate the length for the entire string
        float totalLengthReal = 0;

        //pre-poll the actual length
        for (char letter : text.toCharArray()) {
            float[] thisCharacterArray = translateCharToArray(letter);
            totalLengthReal += thisCharacterArray[4] + 0.1f;
        }
        totalLengthReal -= 0.1f;

        //x is the actual position in the mesh creation of the letter
        //divide the actual length before it's created, start the typewriter
        //half way across
        float x = -totalLengthReal/2f;

        //get the amount of letters in the string
        int stringLength = text.length();

        float[] positions = new float[stringLength * 12 * 2];
        float[] textureCoord = new float[stringLength * 8 * 2];
        int[] indices = new int[stringLength * 6 * 2];
        float[] light = new float[stringLength * 12 * 2];

        int indicesCount = 0;
        int i = 0; //positions count
        int a = 0; //light count
        int w = 0; //textureCoord count
        int t = 0; //indices count


        //foreground
        for (char letter : text.toCharArray()) {

            //translate the character (char primitive) into a usable float array
            float[] thisCharacterArray = translateCharToArray(letter);

            positions[i     ] = (x + thisCharacterArray[4]);
            positions[i + 1 ] = (0.5f);
            positions[i + 2 ] = (0f);

            positions[i + 3 ] = (x);
            positions[i + 4 ] = (0.5f);
            positions[i + 5 ] = (0f);

            positions[i + 6 ] = (x);
            positions[i + 7 ] = (-0.5f);
            positions[i + 8 ] = (0f);

            positions[i + 9 ] = (x + thisCharacterArray[4]);
            positions[i + 10] = (-0.5f);
            positions[i + 11] = (0f);
            i += 12;

            for (int q = 0; q < 4; q++) {
                light[a    ] = (r);
                light[a + 1] = (g);
                light[a + 2] = (b);
                a += 3;
            }

            indices[t    ] = (indicesCount);
            indices[t + 1] = (1 + indicesCount);
            indices[t + 2] = (2 + indicesCount);
            indices[t + 3] = (indicesCount);
            indices[t + 4] = (2 + indicesCount);
            indices[t + 5] = (3 + indicesCount);

            t += 6;
            indicesCount += 4;


            textureCoord[w    ] = (thisCharacterArray[1]);
            textureCoord[w + 1] = (thisCharacterArray[2]);
            textureCoord[w + 2] = (thisCharacterArray[0]);
            textureCoord[w + 3] = (thisCharacterArray[2]);
            textureCoord[w + 4] = (thisCharacterArray[0]);
            textureCoord[w + 5] = (thisCharacterArray[3]);
            textureCoord[w + 6] = (thisCharacterArray[1]);
            textureCoord[w + 7] = (thisCharacterArray[3]);
            w += 8;

            //shift the left of the letter to the right
            //kind of like a type writer

            //make this use the 4th float array variable to space properly
            //add 0.1f so that characters are not squished together

            x += thisCharacterArray[4] + 0.1f;
        }


        //reset x position like a type writer
        x = -totalLengthReal/2f + 0.075f;
        float y = -0.075f;

        //shadow
        for (char letter : text.toCharArray()) {
            //translate the character (char primitive) into a usable float array
            float[] thisCharacterArray = translateCharToArray(letter);

            positions[i     ] = (x + thisCharacterArray[4]);
            positions[i + 1 ] = (0.5f + y);
            positions[i + 2 ] = (0f);

            positions[i + 3 ] = (x);
            positions[i + 4 ] = (0.5f + y);
            positions[i + 5 ] = (0f);

            positions[i + 6 ] = (x);
            positions[i + 7 ] = (-0.5f + y);
            positions[i + 8 ] = (0f);

            positions[i + 9 ] = (x + thisCharacterArray[4]);
            positions[i + 10] = (-0.5f + y);
            positions[i + 11] = (0f);
            i += 12;

            for (int q = 0; q < 4; q++) {
                light[a    ] = (0);
                light[a + 1] = (0);
                light[a + 2] = (0);
                a += 3;
            }

            indices[t    ] = (indicesCount);
            indices[t + 1] = (1 + indicesCount);
            indices[t + 2] = (2 + indicesCount);
            indices[t + 3] = (indicesCount);
            indices[t + 4] = (2 + indicesCount);
            indices[t + 5] = (3 + indicesCount);

            t += 6;
            indicesCount += 4;


            textureCoord[w    ] = (thisCharacterArray[1]);
            textureCoord[w + 1] = (thisCharacterArray[2]);
            textureCoord[w + 2] = (thisCharacterArray[0]);
            textureCoord[w + 3] = (thisCharacterArray[2]);
            textureCoord[w + 4] = (thisCharacterArray[0]);
            textureCoord[w + 5] = (thisCharacterArray[3]);
            textureCoord[w + 6] = (thisCharacterArray[1]);
            textureCoord[w + 7] = (thisCharacterArray[3]);
            w += 8;

            //shift the left of the letter to the right
            //kind of like a type writer

            //make this use the 4th float array variable to space properly
            //add 0.1f so that characters are not squished together

            x += thisCharacterArray[4] + 0.1f;
        }

        return new Mesh(positions, light, indices, textureCoord, fontTextureAtlas);
    }



    //this one is not centered (goes from the center to the right)
    public Mesh createText(String text, float r, float g, float b){

        //x is the actual position in the mesh creation of the letter
        float x = 0;

        //get the amount of letters in the string
        int stringLength = text.length();

        float[] positions = new float[stringLength * 12];
        float[] textureCoord = new float[stringLength * 8];
        int[] indices = new int[stringLength * 6];

        float[] light = new float[stringLength * 12];

        int indicesCount = 0;
        int i = 0; //positions count
        int a = 0; //light count
        int w = 0; //textureCoord count
        int t = 0; //indices count

        for (char letter : text.toCharArray()) {

            //translate the character (char primitive) into a usable float array
            float[] thisCharacterArray = translateCharToArray(letter);

            positions[i     ] = (x + thisCharacterArray[4]);
            positions[i + 1 ] = (0.5f);
            positions[i + 2 ] = (0f);

            positions[i + 3 ] = (x);
            positions[i + 4 ] = (0.5f);
            positions[i + 5 ] = (0f);

            positions[i + 6 ] = (x);
            positions[i + 7 ] = (-0.5f);
            positions[i + 8 ] = (0f);

            positions[i + 9 ] = (x + thisCharacterArray[4]);
            positions[i + 10] = (-0.5f);
            positions[i + 11] = (0f);
            i += 12;

            for (int q = 0; q < 4; q++) {
                light[a    ] = (r);
                light[a + 1] = (g);
                light[a + 2] = (b);
                a += 3;
            }

            indices[t    ] = (indicesCount);
            indices[t + 1] = (1 + indicesCount);
            indices[t + 2] = (2 + indicesCount);
            indices[t + 3] = (indicesCount);
            indices[t + 4] = (2 + indicesCount);
            indices[t + 5] = (3 + indicesCount);

            t += 6;
            indicesCount += 4;


            textureCoord[w    ] = (thisCharacterArray[1]);
            textureCoord[w + 1] = (thisCharacterArray[2]);
            textureCoord[w + 2] = (thisCharacterArray[0]);
            textureCoord[w + 3] = (thisCharacterArray[2]);
            textureCoord[w + 4] = (thisCharacterArray[0]);
            textureCoord[w + 5] = (thisCharacterArray[3]);
            textureCoord[w + 6] = (thisCharacterArray[1]);
            textureCoord[w + 7] = (thisCharacterArray[3]);
            w += 8;

            //shift the left of the letter to the right
            //kind of like a type writer

            //make this use the 4th float array variable to space properly
            //add 0.1f so that characters are not squished together

            x += thisCharacterArray[4] + 0.1f;
        }

        return new Mesh(positions, light, indices, textureCoord, fontTextureAtlas);
    }

    public Mesh createTextWithShadow(String text, float r, float g, float b){

        //x is the actual position in the mesh creation of the letter
        //divide the actual length before it's created, start the typewriter
        //halfway across
        float x = 0;

        //get the amount of letters in the string
        int stringLength = text.length();

        float[] positions = new float[stringLength * 12 * 2];
        float[] textureCoord = new float[stringLength * 8 * 2];
        int[] indices = new int[stringLength * 6 * 2];
        float[] light = new float[stringLength * 12 * 2];

        int indicesCount = 0;
        int i = 0; //positions count
        int a = 0; //light count
        int w = 0; //textureCoord count
        int t = 0; //indices count


        //foreground
        for (char letter : text.toCharArray()) {

            //translate the character (char primitive) into a usable float array
            float[] thisCharacterArray = translateCharToArray(letter);

            positions[i     ] = (x + thisCharacterArray[4]);
            positions[i + 1 ] = (0.5f);
            positions[i + 2 ] = (0f);

            positions[i + 3 ] = (x);
            positions[i + 4 ] = (0.5f);
            positions[i + 5 ] = (0f);

            positions[i + 6 ] = (x);
            positions[i + 7 ] = (-0.5f);
            positions[i + 8 ] = (0f);

            positions[i + 9 ] = (x + thisCharacterArray[4]);
            positions[i + 10] = (-0.5f);
            positions[i + 11] = (0f);
            i += 12;

            for (int q = 0; q < 4; q++) {
                light[a    ] = (r);
                light[a + 1] = (g);
                light[a + 2] = (b);
                a += 3;
            }

            indices[t    ] = (indicesCount);
            indices[t + 1] = (1 + indicesCount);
            indices[t + 2] = (2 + indicesCount);
            indices[t + 3] = (indicesCount);
            indices[t + 4] = (2 + indicesCount);
            indices[t + 5] = (3 + indicesCount);

            t += 6;
            indicesCount += 4;


            textureCoord[w    ] = (thisCharacterArray[1]);
            textureCoord[w + 1] = (thisCharacterArray[2]);
            textureCoord[w + 2] = (thisCharacterArray[0]);
            textureCoord[w + 3] = (thisCharacterArray[2]);
            textureCoord[w + 4] = (thisCharacterArray[0]);
            textureCoord[w + 5] = (thisCharacterArray[3]);
            textureCoord[w + 6] = (thisCharacterArray[1]);
            textureCoord[w + 7] = (thisCharacterArray[3]);
            w += 8;

            //shift the left of the letter to the right
            //kind of like a type writer

            //make this use the 4th float array variable to space properly
            //add 0.1f so that characters are not squished together

            x += thisCharacterArray[4] + 0.1f;
        }


        //reset x position like a typewriter
        x = 0.075f;
        float y = -0.075f;

        //shadow
        for (char letter : text.toCharArray()) {
            //translate the character (char primitive) into a usable float array
            float[] thisCharacterArray = translateCharToArray(letter);

            positions[i     ] = (x + thisCharacterArray[4]);
            positions[i + 1 ] = (0.5f + y);
            positions[i + 2 ] = (0f);

            positions[i + 3 ] = (x);
            positions[i + 4 ] = (0.5f + y);
            positions[i + 5 ] = (0f);

            positions[i + 6 ] = (x);
            positions[i + 7 ] = (-0.5f + y);
            positions[i + 8 ] = (0f);

            positions[i + 9 ] = (x + thisCharacterArray[4]);
            positions[i + 10] = (-0.5f + y);
            positions[i + 11] = (0f);
            i += 12;

            for (int q = 0; q < 4; q++) {
                light[a    ] = (0);
                light[a + 1] = (0);
                light[a + 2] = (0);
                a += 3;
            }

            indices[t    ] = (indicesCount);
            indices[t + 1] = (1 + indicesCount);
            indices[t + 2] = (2 + indicesCount);
            indices[t + 3] = (indicesCount);
            indices[t + 4] = (2 + indicesCount);
            indices[t + 5] = (3 + indicesCount);

            t += 6;
            indicesCount += 4;


            textureCoord[w    ] = (thisCharacterArray[1]);
            textureCoord[w + 1] = (thisCharacterArray[2]);
            textureCoord[w + 2] = (thisCharacterArray[0]);
            textureCoord[w + 3] = (thisCharacterArray[2]);
            textureCoord[w + 4] = (thisCharacterArray[0]);
            textureCoord[w + 5] = (thisCharacterArray[3]);
            textureCoord[w + 6] = (thisCharacterArray[1]);
            textureCoord[w + 7] = (thisCharacterArray[3]);
            w += 8;

            //shift the left of the letter to the right
            //kind of like a typewriter

            //make this use the 4th float array variable to space properly
            //add 0.1f so that characters are not squished together

            x += thisCharacterArray[4] + 0.1f;
        }

        return new Mesh(positions, light, indices, textureCoord, fontTextureAtlas);
    }
}
