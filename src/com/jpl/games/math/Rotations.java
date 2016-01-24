package com.jpl.games.math;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jpereda, April 2014 - @JPeredaDnr
 */
public class Rotations {

    /*
     Each of the 27 cubies are stored as a volume in a three dimensional array of indexes
     These indexes are related to number in the name of the block of the 3D model, as
     they are marked as "Block46", "Block46 (2)",...,"Block72 (6)" (see Model3D)
    
     The initial position refers to:    
     (U)up White, (F)front Blue, (R)right Green, (L)left Red, (D)down Yellow, (B)back Orange
     - first 9 indexes are the 9 cubies in (F)Front face, from top left (R/W/B) to down right (Y/O/B)
     - second 9 indexes are from (S)Standing, from top left (R/W) to down right (Y/O)
     - last 9 indexes are from (B)Back, from top left (G/R/W) to down right (G/Y/O)
     */
    private final int[][][] cube = {{{50, 51, 52}, {49, 54, 53}, {59, 48, 46}},
    {{58, 55, 60}, {57, 62, 61}, {47, 56, 63}},
    {{67, 64, 69}, {66, 71, 70}, {68, 65, 72}}};
    private final Integer[][] colors = {
        {
            1, 1, 1, 1, 1, 1, 1, 1, 1 //
        },
        {
            2, 2, 2, 2, 2, 2, 2, 2, 2
        },
        {
            3, 3, 3, 3, 3, 3, 3, 3, 3
        },
        {
            4, 4, 4, 4, 4, 4, 4, 4, 4
        },
        {
            5, 5, 5, 5, 5, 5, 5, 5, 5
        },
        {
            6, 6, 6, 6, 6, 6, 6, 6, 6
        }
    };

    private final int[][][] tempCube = new int[3][3][3];
    private final Integer[][] tempColors = new Integer[6][9];

    public Rotations() {
        for (int f = 0; f < 3; f++) {
            for (int l = 0; l < 3; l++) {
                System.arraycopy(cube[f][l], 0, tempCube[f][l], 0, 3);
            }
        }
    }

    /* returns 3D array as a flatten list of indexes */
    public List<Integer> getCube() {
        List<Integer> newArray = new ArrayList<>(27);
        for (int f = 0; f < 3; f++) {
            for (int l = 0; l < 3; l++) {
                for (int a = 0; a < 3; a++) {
                    newArray.add(cube[f][l][a]);
                }
            }
        }
        return newArray;
    }

    public void setCube(List<Integer> order) {
        int index = 0;
        for (int f = 0; f < 3; f++) {
            for (int l = 0; l < 3; l++) {
                for (int a = 0; a < 3; a++) {
                    cube[f][l][a] = order.get(index++); //order es una lista plana con los valores de los 27 cubitos, pero que orden?
                    tempCube[f][l][a] = cube[f][l][a];
                }
            }
        }
    }

    /* copy tempCube data in cube */
    public void save() {
        for (int f = 0; f < 3; f++) {
            for (int l = 0; l < 3; l++) {
                System.arraycopy(tempCube[f][l], 0, cube[f][l], 0, 3);//aqui se reemplazan los valores de la lista cube por los modificados en tempCube
            }
        }
    }

    /* print 3D array as a flatten list of indexes in groups of 9 cubies (Front - Standing - Back) */
    public void printCube() {
        List<Integer> newArray = getCube();
        for (int i = 0; i < 27; i++) {
            if (i == 9 || i == 18) {
                System.out.print(" ||");
            }
            System.out.print(" " + newArray.get(i));
        }
        System.out.println("");
    }

    /*
     This is the method to perform any rotation on the 3D array just by swapping indexes
     - first index refers to faces F-S-B
     - second index refers to faces U-E-D
     - third index refers to faces L-M-R
    
     For notation check http://en.wikipedia.org/wiki/Rubik%27s_Cube
     For clockwise rotations Capital letters are used, for counter-clockwise rotation an "i" is
     appended, instead of a ' or a lower letter.
     */
    public void turn(String rot) {
        if (rot.contains("X") || rot.contains("Y") || rot.contains("Z")) {
            for (int z = 0; z < 3; z++) {
                int t = 0;
                for (int y = 2; y >= 0; --y) {
                    for (int x = 0; x < 3; x++) {
                        switch (rot) {
                            case "X":
                                tempCube[t][x][z] = cube[x][y][z];
                                break;
                            case "Xi":
                                tempCube[x][t][z] = cube[y][x][z];
                                break;
                            case "Y":
                                tempCube[t][z][x] = cube[x][z][y];
                                break;
                            case "Yi":
                                tempCube[x][z][t] = cube[y][z][x];
                                break;
                            case "Z":
                                tempCube[z][x][t] = cube[z][y][x];
                                break;
                            case "Zi":
                                tempCube[z][t][x] = cube[z][x][y];
                                break;
                        }
                    }
                    t++;
                }
            }
        } else {
            int t = 0;
            for (int y = 2; y >= 0; --y) {
                for (int x = 0; x < 3; x++) {
                    switch (rot) {
                        case "L":
                            tempCube[x][t][0] = cube[y][x][0];
                            break;
                        case "Li":
                            tempCube[t][x][0] = cube[x][y][0];
                            break;
                        //case "M":  tempCube[x][t][1] = cube[y][x][1]; break;
                        //case "Mi": tempCube[t][x][1] = cube[x][y][1]; break;
                        case "R":
                            tempCube[t][x][2] = cube[x][y][2];
                            break;
                        case "Ri":
                            tempCube[x][t][2] = cube[y][x][2];
                            break;
                        case "U":
                            tempCube[t][0][x] = cube[x][0][y];
                            break;
                        case "Ui":
                            tempCube[x][0][t] = cube[y][0][x];
                            break;
                        //case "E":  tempCube[x][1][t] = cube[y][1][x]; break;
                        //case "Ei": tempCube[t][1][x] = cube[x][1][y]; break;
                        case "D":
                            tempCube[x][2][t] = cube[y][2][x];
                            break;
                        case "Di":
                            tempCube[t][2][x] = cube[x][2][y];
                            break;
                        case "F":
                            tempCube[0][x][t] = cube[0][y][x];
                            break;
                        case "Fi":
                            tempCube[0][t][x] = cube[0][x][y];
                            break;
                        //case "S":  tempCube[1][x][t] = cube[1][y][x]; break;
                        //case "Si": tempCube[1][t][x] = cube[1][x][y]; break;
                        case "B":
                            tempCube[2][t][x] = cube[2][x][y];
                            break;
                        case "Bi":
                            tempCube[2][x][t] = cube[2][y][x];
                            break;
                    }
                }
                t++;
            }
        }
        handleColors(rot);
        save();
    }

    private void handleColors(String rot) {
        rotFace(rot);
        rotBand(rot);
    }

    private void rotFace(String rot) {

        switch (rot) {
            case "L": cycle(6);
                break;
            case "Li":
                cycle(6, true);
                break;
                        //case "M":  tempCube[x][t][1] = cube[y][x][1]; break;
            //case "Mi": tempCube[t][x][1] = cube[x][y][1]; break;
            case "R":cycle(4);
                break;
            case "Ri":
                cycle(4, true);
                break;
            case "U":
                cycle(1);
                break;
            case "Ui":
                cycle(1,true);
                break;
                        //case "E":  tempCube[x][1][t] = cube[y][1][x]; break;
            //case "Ei": tempCube[t][1][x] = cube[x][1][y]; break;
            case "D":cycle(2);
                break;
            case "Di":
                cycle(2, true);
                break;
            case "F":
                cycle(3);
                break;
            case "Fi":
                cycle(3, true);
                break;
                        //case "S":  tempCube[1][x][t] = cube[1][y][x]; break;
            //case "Si": tempCube[1][t][x] = cube[1][x][y]; break;
            case "B":
                cycle(5);
                break;
            case "Bi":
                cycle(5, true);
                break;
        }

    }

    private void cycle(int face) {
        cycle(face,false);
    }
    
    private void cycle(int face, boolean inverted){
        if(!inverted){
        tempColors[face][2] = colors[face][0];
        tempColors[face][1] = colors[face][3];
        tempColors[face][0] = colors[face][6];
        tempColors[face][3] = colors[face][7];
        tempColors[face][6] = colors[face][8];
        tempColors[face][7] = colors[face][5];
        tempColors[face][8] = colors[face][2];
        tempColors[face][5] = colors[face][1];
        }else{
        tempColors[face][0] = colors[face][2];
        tempColors[face][3] = colors[face][1];
        tempColors[face][6] = colors[face][0];
        tempColors[face][7] = colors[face][3];
        tempColors[face][8] = colors[face][6];
        tempColors[face][5] = colors[face][7];
        tempColors[face][2] = colors[face][8];
        tempColors[face][1] = colors[face][5];
            
        }
    }

    private void rotBand(String rot) {
        switch (rot) {
            case "L":
                tempColors[3][0]= colors[1][0];
                tempColors[3][3]= colors[1][3];
                tempColors[3][6]= colors[1][6];
                
                tempColors[2][0]= colors[3][0];
                tempColors[2][3]= colors[3][3];
                tempColors[2][6]= colors[3][6];
                
                tempColors[5][0]= colors[2][0];
                tempColors[5][3]= colors[2][3];
                tempColors[5][6]= colors[2][6];
                
                tempColors[1][0]= colors[5][0];
                tempColors[1][3]= colors[5][3];
                tempColors[1][6]= colors[5][6];
                
                break;
            case "Li":
                tempColors[3][0]= colors[2][0];
                tempColors[3][3]= colors[2][3];
                tempColors[3][6]= colors[2][6];
                
                tempColors[2][0]= colors[5][0];
                tempColors[2][3]= colors[5][3];
                tempColors[2][6]= colors[5][6];
                
                tempColors[5][0]= colors[1][0];
                tempColors[5][3]= colors[1][3];
                tempColors[5][6]= colors[1][6];
                
                tempColors[1][0]= colors[3][0];
                tempColors[1][3]= colors[3][3];
                tempColors[1][6]= colors[3][6];
                break;
            //case "M":  tempCube[x][t][1] = cube[y][x][1]; break;
            //case "Mi": tempCube[t][x][1] = cube[x][y][1]; break;
            case "R":
                tempColors[3][2]= colors[1][2];
                tempColors[3][5]= colors[1][5];
                tempColors[3][8]= colors[1][8];
               
                tempColors[2][2]= colors[3][2];
                tempColors[2][5]= colors[3][5];
                tempColors[2][8]= colors[3][8];
                
                tempColors[5][2]= colors[2][2];
                tempColors[5][5]= colors[2][5];
                tempColors[5][8]= colors[2][8];
                
                tempColors[1][2]= colors[5][2];
                tempColors[1][5]= colors[5][5];
                tempColors[1][8]= colors[5][8];
                break;
            case "Ri":
                tempColors[3][2]= colors[2][2];
                tempColors[3][5]= colors[2][5];
                tempColors[3][8]= colors[2][8];
                
                tempColors[2][2]= colors[5][2];
                tempColors[2][5]= colors[5][5];
                tempColors[2][8]= colors[5][8];
                
                tempColors[5][2]= colors[1][2];
                tempColors[5][5]= colors[1][5];
                tempColors[5][8]= colors[1][8];
                
                tempColors[1][2]= colors[3][2];
                tempColors[1][5]= colors[3][5];
                tempColors[1][8]= colors[3][8];
                break;
            case "U":
                tempColors[6][2]= colors[3][0];
                tempColors[6][5]= colors[3][1];
                tempColors[6][8]= colors[3][2];
                
                tempColors[5][8]= colors[6][2];
                tempColors[5][7]= colors[6][5];
                tempColors[5][6]= colors[6][8];
                
                tempColors[4][0]= colors[5][6];
                tempColors[4][3]= colors[5][7];
                tempColors[4][6]= colors[5][8];
                
                tempColors[3][2]= colors[4][0];
                tempColors[3][1]= colors[4][3];
                tempColors[3][0]= colors[4][6];
                break;
            case "Ui":
                tempColors[3][0]= colors[6][2];
                tempColors[3][1]= colors[6][5];
                tempColors[3][2]= colors[6][8];
                
                tempColors[6][2]= colors[5][8];
                tempColors[6][5]= colors[5][7];
                tempColors[6][8]= colors[5][6];
                
                tempColors[5][6]= colors[4][0];
                tempColors[5][7]= colors[4][3];
                tempColors[5][8]= colors[4][6];
                
                tempColors[4][0]= colors[3][2];
                tempColors[4][3]= colors[3][1];
                tempColors[4][6]= colors[3][0];
                break;
                case "D":
                tempColors[6][0]= colors[3][6];
                tempColors[6][3]= colors[3][7];
                tempColors[6][6]= colors[3][8];
                
                tempColors[3][6]= colors[4][8];
                tempColors[3][7]= colors[4][5];
                tempColors[3][8]= colors[4][2];
                
                tempColors[4][2]= colors[5][0];
                tempColors[4][5]= colors[5][1];
                tempColors[4][8]= colors[5][2];
                
                tempColors[5][0]= colors[6][6];
                tempColors[5][1]= colors[6][3];
                tempColors[5][2]= colors[6][0];
                break;
                    
            //case "E":  tempCube[x][1][t] = cube[y][1][x]; break;
            //case "Ei": tempCube[t][1][x] = cube[x][1][y]; break;
           
            case "Di":
                tempColors[3][6]= colors[6][0];
                tempColors[3][7]= colors[6][3];
                tempColors[3][8]= colors[6][6];
                
                tempColors[4][8]= colors[3][6];
                tempColors[4][5]= colors[3][7];
                tempColors[4][2]= colors[3][8];
                
                tempColors[5][0]= colors[4][2];
                tempColors[5][1]= colors[4][5];
                tempColors[5][2]= colors[4][8];
                
                tempColors[6][6]= colors[5][0];
                tempColors[6][3]= colors[5][1];
                tempColors[6][0]= colors[5][2];
                break;
                
            case "F":
                tempColors[1][6]= colors[6][6];
                tempColors[1][7]= colors[6][7];
                tempColors[1][8]= colors[6][8];
                
                tempColors[6][6]= colors[2][2];
                tempColors[6][7]= colors[2][1];
                tempColors[6][8]= colors[2][0];
                
                tempColors[2][0]= colors[4][8];
                tempColors[2][1]= colors[4][7];
                tempColors[2][2]= colors[4][6];
                
                tempColors[4][8]= colors[1][8];
                tempColors[4][7]= colors[1][7];
                tempColors[4][6]= colors[1][6];
                break;
            case "Fi":
                tempColors[6][6]= colors[1][6];
                tempColors[6][7]= colors[1][7];
                tempColors[6][8]= colors[1][8];
                
                tempColors[2][2]= colors[6][6];
                tempColors[2][1]= colors[6][7];
                tempColors[2][0]= colors[6][8];
                
                tempColors[4][8]= colors[2][0];
                tempColors[4][7]= colors[2][1];
                tempColors[4][6]= colors[2][2];
                
                tempColors[1][8]= colors[4][8];
                tempColors[1][7]= colors[4][7];
                tempColors[1][6]= colors[4][6];
                break;
            //case "S":  tempCube[1][x][t] = cube[1][y][x]; break;
            //case "Si": tempCube[1][t][x] = cube[1][x][y]; break;
            case "B":
                tempCube[2][t][x] = cube[2][x][y];
                break;
            case "Bi":
                tempCube[2][x][t] = cube[2][y][x];
                break;
        }
    }

}
