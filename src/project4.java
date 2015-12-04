/* Project 3
 * Tyler Diminick
 * n00889574
 * Description: This program will create a hash map
 * that allows a user to hash names and numbers based
 * on the format describe in the text file accompying
 * this program.
 */
import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;


//--------------Symbol Table class-------------------
class SymbolTable {
   final int TBL_SIZE = 134;
   private Symbol[] symbolTable;

   public SymbolTable(Scanner input) {    // constructor
      String line;
      symbolTable = new Symbol[TBL_SIZE];
      for(int i = 0; i < TBL_SIZE; i++) {
         String name = input.next();
         String opCode =  input.next();
         int byteSize = input.nextInt();
         int byteSize2 = input.nextInt();
         int key = myHash(name);
         key = collisionCheck(key, name);
         Symbol temp = new Symbol(name, opCode, byteSize);
         symbolTable[key] = temp;
      }
   }

   public int length() {
      return TBL_SIZE;
   }
   // returns -1 if not found, otherwise the key
   public int getKey(String name) {
      String tempName = name;
      int key = myHash(tempName);
      if (tempName.equals(symbolTable[key].getName()))
         return key;
      int tempNumber = key++;
      while (key != tempNumber) {
         if (tempName.equals(symbolTable[key].getName()))
            return key;
         if (key == TBL_SIZE - 1)
            key = 0;
         else
            key++;
      }
      return -1;
   }

   public int myHash(String name){
      long sumTotal = 0;
      int key;
      for(int i = 0; i < name.length(); i++)    // converting name to numbers
          sumTotal += (long) name.charAt(i);    // and adding them up, then
      key = (int) (sumTotal % TBL_SIZE);              // mod the sum for the hashkey
      return key;
   }
   
//----------------------------------------------------
   public Symbol getSymbol(int key) {
      return symbolTable[key];
   }

//----------------------------------------------------
   public int collisionCheck(int key, String name) {
      int newKey = key;
      if(symbolTable[newKey] == null){
         //no collision
         return newKey;
      } else {         
         while(symbolTable[newKey] != null) {
            if(newKey < TBL_SIZE - 1)
               newKey++;
            else
               newKey = 0;
         }
      }
      return newKey;    // if the slot is open, return positive hash key
   }

   public void displayTable() {
      for(int i = 0; i < TBL_SIZE; i++)
         System.out.println(i + ": " + symbolTable[i].toString());
   }
}

//----------------------------------------------------
//----------End of SymbolTable Class--------------------
//----------------------------------------------------           

class LabelTable {
   final int TABLE_SIZE = 251;
   Label[] labelTable;

   public LabelTable() {
      this.labelTable = new Label[TABLE_SIZE];
   }

   public int add(String name, hex address) {
      int key = myHash(name);
      key = collisionCheck(key, name);
      if(key != -1)
         labelTable[key] = new Label(name, address);
      else
         return -1;
      return key;
   }

   public int myHash(String name){
      long sumTotal = 0;
      int key;
      for(int i = 0; i < name.length(); i++)    // converting name to numbers
          sumTotal += (long) name.charAt(i);    // and adding them up, then
      key = (int) (sumTotal % TABLE_SIZE);              // mod the sum for the hashkey
      return key;
   }

   public int collisionCheck(int key, String name) {
      int newKey = key;
      if(labelTable[newKey] == null){
         //no collision
         return newKey;
      } else {         
         while(labelTable[newKey] != null) {
            if (labelTable[newKey].getName().equals(name))
               return -1;
            if(newKey < TABLE_SIZE - 1)
               newKey++;
            else
               newKey = 0;
         }
      }
      return newKey;    // if the slot is open, return positive hash key
   }

   public int getKey(String name) {
      String tempName = name;
      int key = myHash(tempName);
      if (tempName.equals(labelTable[key].getName()))
         return key;
      int tempNumber = key++;
      while (key != tempNumber) {
         if (tempName.equals(labelTable[key].getName()))
            return key;
         if (key == TABLE_SIZE - 1)
            key = 0;
         else
            key++;
      }
      return -1;
   }

   public Label getLabel(int key) {
      return labelTable[key];
   }
}

class Label {
   private String name;
   private hex address;

   public Label(String name, hex address) {
      this.name = name;
      this.address = address;
   }

   public void setAddress(hex address) {
      this.address = address;
   }

   public String getName() { return name; }
   public hex getAddress() { return address; }

}

public class project4 {

   public static void main(String[] args) throws FileNotFoundException {
      File sicOps = new File(args[0]);
      File sicIns = new File(args[1]);
      String fileName = sicIns.getName(); 
      Scanner input = new Scanner(sicOps);
      Scanner input2 = new Scanner(sicIns);
      SymbolTable symbolTable = new SymbolTable(input);
      ArrayList<String> registers = new ArrayList<String>();
      ArrayList<instruction> instructions = new ArrayList<instruction>();
      int i = 0;
      while (input2.hasNext()) {
         instructions.add(i, new instruction(input2.nextLine()));
         i++;
      }
      registers.add(0, "A");
      registers.add(1, "X");
      registers.add(2, "L");
      registers.add(3, "B");
      registers.add(4, "S");
      registers.add(5, "T");
      registers.add(6, "F");
      registers.add(7, "");
      registers.add(8, "PC");
      registers.add(9, "SW");
      input.close();
      input2.close();
      assembler assem = new assembler(symbolTable, instructions, fileName, registers);
      assem.run();
   }
   
//----------------------------------------------------------
   
   public static int myHash(String name, SymbolTable symbolTable){
      long sumTotal = 0;
      int key;
      for(int i = 0; i < name.length(); i++)    // converting name to numbers
          sumTotal += (long) name.charAt(i);    // and adding them up, then
      key = (int) (sumTotal % symbolTable.TBL_SIZE);              // mod the sum for the hashkey
      return key;
   }

}
//------------------------------------------------------------

class Symbol {

   private String name;
   private String opCode;
   private int byteSize;

   public Symbol(String name, String opCode, int byteSize) {
      this.name = name;
      this.opCode = opCode;
      this.byteSize = byteSize;
   }

   public String getName() {
      return name;
   }

   public String getOpCode() {
      return opCode;
   }

   public int getByteSize() {
      return byteSize;
   }

   public String toString() {
      return name + " " + opCode + " " + String.valueOf(byteSize);
   }
}

class instruction {
   private String label = "         ";
   private String name = "         ";
   private String operand = "           ";
   private String comment = "";
   private boolean isComment = false;
   private boolean isDuplicateLabel = false;
   private boolean lostLabel = false;
   private boolean missingBase = false;
   private boolean baseOutOfRange = false;
   private hex address;
   private hex objectCode = new hex("      ");

   public instruction(String input) {
      if(input.charAt(0) == '.'){
         isComment = true;
         label = "";
         name = "";
         operand = "";
         comment = input;
      } else if (input.length() < 18) {
         label = input.substring(0,9);
         name = input.substring(9,input.length());
         operand = "        ";
         comment = "";
      } else if (input.length() < 30) {
         label = input.substring(0,9);
         name = input.substring(9,18);
         operand = input.substring(18,input.length());
         comment = "";
      } else {
         label = input.substring(0,9);
         name = input.substring(9,18);
         operand = input.substring(18,29);
         comment = input.substring(29);
      }
   }

   public String toString() {
      if (isComment)
         return comment;
      else
         return label + name + operand + comment;
   }

   public void setIsDuplicateLabel(boolean isDuplicateLabel) {
      this.isDuplicateLabel = isDuplicateLabel;
   }

   public void setLostLabel(boolean lostLabel) {
      this.lostLabel = lostLabel;
   }

   public void setMissingBase(boolean missingBase) {
      this.missingBase = missingBase;
   }

   public void setBaseOutOfRange(boolean baseOutOfRange) {
      this.baseOutOfRange = baseOutOfRange;
   }

   public void setAddress(hex address) {
      this.address = address;
   }

   public void setObjectCode(hex objectCode) {
      this.objectCode = objectCode;
   }

   public String getLabel() { return label; }
   public String getName() { return name; }
   public String getOperand() { return operand; }
   public String getComment() { return comment; }
   public boolean getIsComment() { return isComment; }
   public boolean getIsDuplicateLabel() { return isDuplicateLabel; }
   public boolean getLostLabel() { return lostLabel; }
   public boolean getMissingBase() { return missingBase; }
   public boolean getBaseOutOfRange() { return baseOutOfRange; }
   public hex getAddress() { return address; }
   public hex getObjectCode() { return objectCode; }
}

class assembler {
   private SymbolTable symbolTable;
   private PrintWriter writer;
   private PrintWriter writer2;
   private ArrayList<instruction> instructions;
   private String fileName;
   private ArrayList<String> registers;

   public assembler(SymbolTable symbolTable, ArrayList<instruction> instructions, String fileName, ArrayList<String> registers) throws FileNotFoundException {
      this.symbolTable = symbolTable;
      this.instructions = instructions;
      this.fileName = fileName;
      this.writer = new PrintWriter(fileName + ".lst");
      this.writer2 = new PrintWriter(fileName + ".obj");
      this.registers = registers;
   } 

   public void run() {
      writeFile();
      writer.close();
      writer2.close();
   }

   public void writeFile() {
      int lineNo = 1;
      hex address = new hex("00000");
      boolean hasStart = false;
      ArrayList<instruction> literals = new ArrayList<>();
      LabelTable labelTable = new LabelTable();
      boolean hasLTORG = false;
      boolean hasBase = false;
      hex baseAddress = new hex("      ");
      String baseAddressFlag = "";
      boolean hasDuplicateLabel = false;
      writer.println("***********************************");
      writer.println("SIC/XE assembler");
      writer.println("version date March 18, 2015");
      writer.println("Author: Tyler Diminick :: n00889574");
      writer.println("***********************************");
      writer.println("ASSEMBLER REPORT");
      writer.println("----------------");
      writer.println("\t  LOC   OBJECT CODE \t \t SOURCE CODE");
      writer.println("\t  ---   ----------- \t \t -----------");
      
      // gets address's for each instruction
      for(int i = 0; i < instructions.size(); i++) {
         if (!instructions.get(i).getIsComment()) {
            String tempLabel = instructions.get(i).getLabel().trim();
            String tempName = instructions.get(i).getName().trim();
            String tempOper = instructions.get(i).getOperand().trim();
            int hashKey = symbolTable.getKey(tempName);
            if (tempName.equals("START")) {
               hasStart = true;
               if (i == 0) {
                  address = new hex(tempOper); 
                  instructions.get(i).setAddress(address);
               }
            }

            if(!tempLabel.equals("")) {
               int result = labelTable.add(tempLabel, address);
               if(result == -1) {
                  hasDuplicateLabel = true;
                  instructions.get(i).setIsDuplicateLabel(true);
               }
            }
            if ( !tempOper.equals("") && tempOper.substring(0,1).equals("=")) {
               literals.add(instructions.get(i));
               labelTable.add(tempOper, address);
               hasLTORG = true;
            }
            if (tempName.equals("RESW")) {
               instructions.get(i).setAddress(address);
               address = getNextAddress(address, symbolTable.getSymbol(hashKey), Integer.parseInt(instructions.get(i).getOperand().trim()));
            }
            else if (hasStart && hashKey != -1 && i > 0) {
               instructions.get(i).setAddress(address);
               address = getNextAddress(address, symbolTable.getSymbol(hashKey), 1);
            }
            if (tempName.equals("LTORG")) {
               instructions.get(i).setAddress(address);
               address = printLiterals(literals, address, false, labelTable);
            }

            if (tempName.equals("BASE")) {
               hasBase = true;
               instructions.get(i).setAddress(new hex("00000"));
            }
         }
      }

      if(hasDuplicateLabel) {
         // print with no object, done for duplicate label files
         address = new hex("00000");
         for(int i = 0; i < instructions.size(); i++) {
            writer.print(String.format("%03d- ", lineNo));
            if (instructions.get(i).getIsComment())
               writer.println("\t \t \t \t \t \t \t \t" + instructions.get(i).toString());
            else {
               String tempName = instructions.get(i).getName().trim();
               int hashKey = symbolTable.getKey(tempName);
               if (tempName.equals("START")) {
                  hasStart = true;
                  if (i == 0)
                     address = new hex(instructions.get(i).getOperand().trim()); 
               }
               writer.print(instructions.get(i).getAddress().getAHex());
               writer.print("\t \t \t \t \t \t ");
               writer.println(instructions.get(i).toString());

               if (instructions.get(i).getOperand().substring(0,1).equals("=")) {
                  literals.add(instructions.get(i));
                  hasLTORG = true;
               }
               if (tempName.equals("RESW"))
                  address = getNextAddress(address, symbolTable.getSymbol(hashKey), Integer.parseInt(instructions.get(i).getOperand().trim()));
               else if (hasStart && hashKey != -1 && i > 0)
                  address = getNextAddress(address, symbolTable.getSymbol(hashKey), 1);
               if (tempName.equals("LTORG")) {
                  address = printLiterals(literals, address, true, labelTable);
                  literals.clear();
                  hasLTORG = false;
               }
               if (!hasStart)
                  writer.println("********** WARNING: START statement not found [on line " + lineNo + "]");
               if (hashKey == -1)
                  writer.println("********** ERROR: Unsupported opcode found in statement [on line " + lineNo + "]");
               if(instructions.get(i).getIsDuplicateLabel())
                  writer.println("********** ERROR: Duplicate label found [on line " + lineNo + "]");
            }
            lineNo++;
         }
      } else {
         for(int i = 0; i < instructions.size(); i++) {
            String tempLabel = instructions.get(i).getLabel().trim();
            String tempName = instructions.get(i).getName().trim();
            String tempOper = instructions.get(i).getOperand().trim();
            if (tempName.equals("LDB") || tempName.equals("+LDB")) {
               if(tempOper.charAt(0) == '#' || tempOper.charAt(0) == '@')
                  tempOper = tempOper.substring(1,tempOper.length());
               int tempKey = labelTable.getKey(tempOper);
               if (tempKey > -1)
                  baseAddress = new hex(labelTable.getLabel(tempKey).getAddress().getHex());
            }
         }

         // set object codes in instructions
         address = new hex("00000");
         for(int i = 0; i < instructions.size(); i++) {
            if (!instructions.get(i).getIsComment()) {
               String tempLabel = instructions.get(i).getLabel().trim();
               String tempName = instructions.get(i).getName().trim();
               String tempOper = instructions.get(i).getOperand().trim();
               int hashKey = symbolTable.getKey(tempName);
               hex tempOpCode = new hex(symbolTable.getSymbol(hashKey).getOpCode().trim());
               if (tempName.equals("RESW") || tempName.equals("WORD")) {
                  instructions.get(i).setObjectCode(new hex("      "));
                  address = getNextAddress(address, symbolTable.getSymbol(hashKey), Integer.parseInt(instructions.get(i).getOperand().trim()));
               } else if(tempName.equals("END")) {
                  instructions.get(i).setObjectCode(new hex("      "));
                  address = getNextAddress(address, symbolTable.getSymbol(hashKey), 0);
               } else if (hasStart && hashKey != -1 && i > 0 && tempOper.length() > 0) {
                  address = getNextAddress(address, symbolTable.getSymbol(hashKey), 1);
                  if(tempOper.charAt(0) == '#')
                     tempOpCode = new hex(tempOpCode.getDec() + 1);
                  else if (tempOper.charAt(0) == '@')
                     tempOpCode = new hex(tempOpCode.getDec() + 2);
                  else
                     tempOpCode = new hex(tempOpCode.getDec() + 3);
                  if(tempOper.length() > 2 && tempOper.substring(tempOper.length() - 2, tempOper.length()).equals(",X")) {
                     tempOpCode = new hex(tempOpCode.get2Hex() + "8");
                     tempOper = tempOper.substring(0, tempOper.length() - 2);
                  }
                  else
                     tempOpCode = new hex(tempOpCode.get2Hex() + "0");
                  if(tempName.charAt(0) == '+')
                     tempOpCode = new hex(tempOpCode.getDec() + 1);
                  else
                     tempOpCode = new hex(tempOpCode.getDec() + 2);
               }
               if (tempName.equals("START")) {
                  hasStart = true;
                  if (i == 0) {
                     address = new hex(tempOper); 
                     instructions.get(i).setObjectCode(new hex("      "));
                  }
               }
               Symbol tempSymbol = symbolTable.getSymbol(symbolTable.getKey(tempName));
               if(tempSymbol.getByteSize() == 2) {
                  String addy = new String(tempSymbol.getOpCode());
                  if(tempOper.length() == 3) {
                     addy += Integer.toString(registers.indexOf(tempOper.substring(0,1))) + Integer.toString(registers.indexOf(tempOper.substring(2,3)));
                  }
                  else if(tempOper.length() == 5)
                     addy += Integer.toString(registers.indexOf(tempOper.substring(0,2))) + Integer.toString(registers.indexOf(tempOper.substring(3,5)));
                  else if(tempOper.charAt(1) == ',')
                     addy += Integer.toString(registers.indexOf(tempOper.substring(0,1))) + Integer.toString(registers.indexOf(tempOper.substring(2,4)));
                  else
                     addy += Integer.toString(registers.indexOf(tempOper.substring(0,2))) + Integer.toString(registers.indexOf(tempOper.substring(3,4)));
                  instructions.get(i).setObjectCode(new hex(addy));
               } else if(tempSymbol.getByteSize() == 0 || tempSymbol.getByteSize() == 1) {
                  instructions.get(i).setObjectCode(new hex("      "));
               } else if(tempSymbol.getByteSize() == 3 && !tempName.equals("RESW")) {
                  if(tempOper.equals("")) {
                     instructions.get(i).setObjectCode(new hex("      "));
                  } else {
                     if (tempOper.charAt(0) == '#' || tempOper.charAt(0) == '@') {
                        tempOper = tempOper.substring(1,tempOper.length());
                     }
                     if(isNumeric(tempOper)) {
                        hex h = new hex(Integer.parseInt(tempOper));
                        if(tempName.equals("WORD"))
                           tempOpCode = new hex(h.get6Hex());
                        else {
                           String s = tempOpCode.get3Hex();
                           s = s.substring(0,2);
                           s = s + "0";
                           tempOpCode = new hex(s + h.get3Hex());
                        }
                        instructions.get(i).setObjectCode(tempOpCode);
                     } else {
                        hex pcAddr = new hex(address.getHex());
                        hex labelAddr;
                        int tempKey = labelTable.getKey(tempOper);
                        if(tempKey == -1) {
                           tempOpCode = new hex("      ");
                           instructions.get(i).setLostLabel(true);
                        } else {
                           labelAddr = new hex(labelTable.getLabel(tempKey).getAddress().getHex());
                           int result = labelAddr.getDec() - pcAddr.getDec();
                           hex relAddr = new hex(result);
                           if(result >= -2048 && result <= 2047) {
                              tempOpCode = new hex(tempOpCode.get3Hex().concat(relAddr.get3Hex()));
                           } else {
                              if(hasBase == false) {
                                 tempOpCode = new hex("      ");
                                 instructions.get(i).setMissingBase(true);
                              }
                              else {
                                 pcAddr = baseAddress;
                                 result = labelAddr.getDec() - pcAddr.getDec();
                                 relAddr = new hex(result);
                                 if(result > -1 && result < 4097) {
                                    tempOpCode = new hex(tempOpCode.get3Hex().concat(relAddr.get3Hex()));
                                 } else {
                                    instructions.get(i).setBaseOutOfRange(true);
                                    tempOpCode = new hex("      ");
                                 }
                              }
                           }
                        }
                     instructions.get(i).setObjectCode(tempOpCode);
                     }
                  }

               } else if(!tempName.equals("RESW")) {
                  if(tempOper.equals("")) {
                     instructions.get(i).setObjectCode(new hex("        "));
                  } else {
                     if (tempOper.charAt(0) == '#' || tempOper.charAt(0) == '@') {
                        tempOper = tempOper.substring(1,tempOper.length());
                     }
                     if(isNumeric(tempOper)) {
                        hex h = new hex(Integer.parseInt(tempOper));
                        tempOpCode = new hex(tempOpCode.get3Hex() + h.getAHex());
                        instructions.get(i).setObjectCode(tempOpCode);
                     } else {
                        hex pcAddr = new hex(address.getHex());
                        hex labelAddr;
                        int tempKey = labelTable.getKey(tempOper);
                        if(tempKey == -1) {
                           tempOpCode = new hex("        ");
                           instructions.get(i).setLostLabel(true);
                        } else {
                           labelAddr = new hex(labelTable.getLabel(tempKey).getAddress().getHex());
                           int result = labelAddr.getDec() - pcAddr.getDec();
                           hex relAddr = new hex(result);
                           tempOpCode = new hex(tempOpCode.get3Hex().concat(relAddr.getAHex()));
                        }
                     instructions.get(i).setObjectCode(tempOpCode);
                     }
                  }
               }

               // these are the checks for random words 
               if (tempName.equals("LTORG")) {
                  instructions.get(i).setObjectCode(new hex("      "));
                  address = printLiterals(literals, address, false, labelTable);
               }

               if(tempName.equals("RSUB")) {
                  instructions.get(i).setObjectCode(new hex("4f0000"));
               }

               if (tempName.equals("BYTE")) 
                  instructions.get(i).setObjectCode(new hex(Integer.parseInt(tempOper, 16)));

               if (tempName.equals("BASE") || tempName.equals("END")) {
                  instructions.get(i).setObjectCode(new hex("      "));
                  address = getNextAddress(address, symbolTable.getSymbol(hashKey), 1);
               }

               if (tempName.equals("BASE")) {
                  hasBase = true;
               }
            }
         }
         // end of object codes


         lineNo = 1;
         // print the lines for the .lst file
         address = new hex("00000");
         for(int i = 0; i < instructions.size(); i++) {
            writer.print(String.format("%03d- ", lineNo));
            if (instructions.get(i).getIsComment())
               writer.println("\t \t \t \t \t \t \t \t" + instructions.get(i).toString());
            else {
               String tempName = instructions.get(i).getName().trim();
               int hashKey = symbolTable.getKey(tempName);
               if (tempName.equals("START")) {
                  hasStart = true;
                  if (i == 0)
                     address = new hex(instructions.get(i).getOperand().trim());
                  writer2.println(address.get6Hex());
                  writer2.println("000000");
               }
               writer.print(address.getAHex());
               writer.print("  ");
               String objectCd = instructions.get(i).getObjectCode().getOHex();
               writer.print(objectCd);
               if(objectCd.length() <= 6)
                  writer.print("\t \t \t \t ");
               else
                  writer.print("\t \t \t ");
               writer.println(instructions.get(i).toString());

               if (tempName.equals("RESW")) {
                  address = getNextAddress(address, symbolTable.getSymbol(hashKey), Integer.parseInt(instructions.get(i).getOperand().trim()));
                  writer2.println("!");
                  writer2.println(address.get6Hex());
                  writer2.println("000000");
               } else if (hasStart && hashKey != -1 && i > 0) {
                  address = getNextAddress(address, symbolTable.getSymbol(hashKey), 1);
               } 

               if (tempName.equals("BASE")) {
                  writer2.println("!");
                  writer2.println(address.get6Hex());
                  writer2.println("000000");
               }
               if(!tempName.equals("RESW") || !tempName.equals("BASE") || objectCd.trim().equals("")) {
                  writer2.println(objectCd);
               }

               if(tempName.equals("END")) {
                  writer2.println(address.get6Hex());
                  String tempOper = instructions.get(i).getOperand().trim();
                  if(tempOper.length() > 0) {
                     int x = labelTable.getKey(tempOper);
                     Label l = labelTable.getLabel(x);
                     writer2.println(l.getAddress().get6Hex());
                     writer2.println("!");
                  } else {
                     writer2.println("000000");
                     writer2.println("!");
                  }
               }
               if (tempName.equals("LTORG")) {
                  address = printLiterals(literals, address, true, labelTable);
                  literals.clear();
                  hasLTORG = false;
               }
               if (!hasStart)
                  writer.println("********** WARNING: START statement not found [on line " + lineNo + "]");
               if (hashKey == -1)
                  writer.println("********** ERROR: Unsupported opcode found in statement [on line " + lineNo + "]");
               if (instructions.get(i).getLostLabel())
                  writer.println("********** ERROR: Label not found [on line " + lineNo + "]");
               if (instructions.get(i).getMissingBase())
                  writer.println("********** ERROR: Displacement out of range for PC relative (NOBASE in effect) [on line " + lineNo + "]");
               if (instructions.get(i).getBaseOutOfRange())
                  writer.println("********** ERROR: Displacement out of range PC relative and for BASE value [on line " + lineNo + "]");
            }
            lineNo++;
         }
      }
      if (hasLTORG) {
         address = printLiterals(literals, address, true, labelTable);
         literals.clear();
         hasLTORG = false;
      }
   }

   public static boolean isNumeric(String str)  
   {  
   try  
   {  
      int d = Integer.parseInt(str);  
   }  
   catch(NumberFormatException nfe)  
   {  
       return false;  
   }  
   return true;  
   }

   public hex printLiterals(ArrayList<instruction> literals, hex address, boolean printL, LabelTable labelTable) {
      for(int i = 0; i < literals.size(); i++) {
         String tempOperand = literals.get(i).getOperand().trim();
         int ind = labelTable.getKey(tempOperand);
         Label tempLabel = labelTable.getLabel(ind);
         String tempName = tempLabel.getName();
         if(printL) {
            writer.print(String.format("+%02d+ ", i + 1));
            writer.print(address.getAHex());
            writer.print("  ");
            int z = 0;
            if(tempName.charAt(1) == 'C') {
               tempName = tempName.substring(3, tempName.length() - 1);
               z = tempName.length();
               for(int j = 0; j < tempName.length(); j++) {
                  writer.print(Integer.toHexString((int)tempName.charAt(j)));
                  writer2.print(Integer.toHexString((int)tempName.charAt(j)));
               }
            } else if (tempName.charAt(1) == 'X') {
               z = tempName.length() / 2;
               tempName = tempName.substring(3, tempName.length() - 1);
               for(int j = 0; j < tempName.length(); j++) {
                  writer.print(tempName.charAt(j));
                  writer2.print(tempName.charAt(j));
               }
            }
            writer2.println("");
            if(z < 4)
               writer.print("\t \t \t \t \t ");
            else if(z < 8)
               writer.print("\t \t \t \t ");
            else
               writer.print("\t \t \t ");
            writer.println(tempOperand + "   BYTE      " + tempOperand.substring(1));
         } else {
            labelTable.getLabel(ind).setAddress(address);
         }
         int multiplier = 1;
         if (tempOperand.charAt(1) == 'C') {
            multiplier = tempOperand.trim().length() - 4;
            literals.get(i).setAddress(address);
            address = getNextAddress(address, symbolTable.getSymbol(symbolTable.getKey("BYTE")), multiplier);
         }
         else if (tempOperand.charAt(1) == 'X') { 
            if ((tempOperand.trim().length() - 4) % 2 == 0) {
               multiplier = (tempOperand.trim().length() - 4) / 2;
               literals.get(i).setAddress(address);
               address = getNextAddress(address, symbolTable.getSymbol(symbolTable.getKey("BYTE")), multiplier);
            }
            else
               writer.println("********** ERROR: Odd number of X bytes found in operand field [ on line +2]");
         }
         else
            writer.println("********** ERROR: Expected X or C before open quote [ on line "+ i + 1 + "]");
      }
      return address;
   }

   public hex getNextAddress(hex address, Symbol symbol, int multiplier) {
      int format = symbol.getByteSize() * multiplier;
      hex newAddress = new hex(address.getDec() + format);
      return newAddress;
   }
}

class hex {
   private String hexValue;
   private int decValue;

   public hex(String input) {
      hexValue = input;
      if(input.charAt(0) != ' ')
         decValue = Integer.parseInt(input, 16);
   }

   public hex(int input) {
      hexValue = Integer.toHexString(input);
      decValue = input;
   }

   public String getHex() {
      return hexValue;
   }

   public String get3Hex() {
      String temp = hexValue;
      if(temp.length() == 3)
         return temp;
      else if(temp.length() < 3)
         while(temp.length() < 3)
            temp = "0" + temp;
      else
         while(temp.length() > 3)
            temp = temp.substring(1);
      return temp;
   }

   public String get2Hex() {
      String temp = hexValue;
      if(temp.length() == 2)
         return temp;
      else if(temp.length() < 2)
         while(temp.length() < 2)
            temp = "0" + temp;
      else
         while(temp.length() > 2)
            temp = temp.substring(1);
      return temp;
   }

   public String get6Hex() {
      String temp = hexValue;
      if(temp.length() == 6)
         return temp;
      else if(temp.length() < 6)
         while(temp.length() < 6)
            if(decValue >= 0)
               temp = "0" + temp;
            else
               temp = "f" + temp;
      else
         while(temp.length() > 6)
            temp = temp.substring(1);
      return temp;
   }

   public String getAHex() {
      String temp = hexValue;
      if (temp.length() == 5)
         return temp.toUpperCase();
      else if (temp.length() < 5)
         while (temp.length() < 5)
            temp = "0" + temp;
      else
         while (temp.length() > 5)
            temp = temp.substring(1);
      return temp.toUpperCase();
   }

   public String getOHex() {
      String temp = hexValue;
      if(temp.length() == 6 || temp.length() == 8)
         return temp;
      else if (temp.length() == 5 || temp.length() == 7)
         temp = "0" + temp;
      return temp;
   }

   public int getDec() {
      return decValue;
   }
}