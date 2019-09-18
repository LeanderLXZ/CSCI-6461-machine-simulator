public class Main {
    public static void main (String[] args){

        Initializer initializer = new Initializer();

        // Initialize all classes
        CiscComputer ciscComputer = initializer.initialize();
        InstructionProcessor instructionProcessor = new InstructionProcessor();
        InstructionRegister instructionRegister = ciscComputer.getInstructionRegister();
        ProgramCounter programCounter = ciscComputer.getProgramCounter();
        DebugPanel dp = new DebugPanel();
        dp.setData(ciscComputer);

        // On address 30 have instruction: LDX 1, 20, in binary format: 1010010001010100
//        for (int address = 30; address <= 39; address++) {
//            programCounter.setDecimalValue(address + 1);
//
//            instructionRegister.setBinaryInstruction(Memory.memoryMap.get(address));
//
//            instructionProcessor.processInstruction(ciscComputer);
//
//            printValues(ciscComputer);
//            dp.setData(ciscComputer);
//            try {
//                Thread.sleep(250);
//            } catch(InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
//        }
//
//        // Save all changes before quit
//        ciscComputer.getMemory().writeContent();
    }

    protected static void printValues(CiscComputer ciscComputer) {
        System.out.println("In Binary  -> " + new Display(ciscComputer, true).toString());
        System.out.println("In Decimal -> " + new Display(ciscComputer, false).toString());
    }
}
