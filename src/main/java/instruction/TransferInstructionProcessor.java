package main.java.instruction;

import main.java.common.CiscComputer;
import main.java.memory.Address;
import main.java.memory.Cache;
import main.java.register.*;

import java.util.List;


/*
 * This class process all the transfer instructions of the machine.
 */
public class TransferInstructionProcessor implements InstructionProcessor {

    private static InstructionProcessor processor;

    static InstructionProcessor getInstance() {
        if (processor == null) {
            processor = new TransferInstructionProcessor();
        }

        return processor;
    }

    @Override
    public void process(CiscComputer ciscComputer, Instruction instruction) {
        Address address = AddressDecoder.decodeAddress(instruction, ciscComputer.getMachineFaultRegister());
        Register firstRegister = instruction.getFirstRegister();

        switch (instruction.getType()) {
            case JZ:
                jumpAddressIfZero(firstRegister, address, ciscComputer);
                break;
            case JNE:
                jumpAddressIfNotZero(firstRegister, address, ciscComputer);
                break;
            case JCC:
                jumpAddressIfConditionCode(firstRegister, ciscComputer.getConditionCode(), address, ciscComputer);
                break;
            case JMA:
                jumpAddress(address, ciscComputer);
                break;
            case JSR:
                jumpAddressSaveReturnAddress(address, ciscComputer);
                break;
            case RFS:
                returnFromSubroutine(address, ciscComputer);
                break;
            case SOB:
                subtractOneAndBranch(firstRegister, address, ciscComputer);
                break;
            case JGE:
                jumpAddressGreaterOrEqual(firstRegister, address, ciscComputer);
                break;
        }

    }

    private void jumpAddressIfZero(Register firstRegister, Address address, CiscComputer ciscComputer) {
        ProgramCounter pc = new ProgramCounter();
//        System.out.println(firstRegister.getDecimalValue());
        pc.setDecimalValue((firstRegister.getDecimalValue() == 0) ? address.getEffectiveAddress() : ciscComputer.getProgramCounter().getDecimalValue());
        ciscComputer.setProgramCounter(pc);
//        System.out.println(ciscComputer.getProgramCounter().getDecimalValue());
    }

    private void jumpAddressIfNotZero(Register firstRegister, Address address, CiscComputer ciscComputer) {
        ProgramCounter pc = new ProgramCounter();
        pc.setDecimalValue((firstRegister.getDecimalValue() != 0) ? address.getEffectiveAddress() : ciscComputer.getProgramCounter().getDecimalValue());
        ciscComputer.setProgramCounter(pc);
    }

    private void jumpAddressIfConditionCode(Register firstRegister, ConditionCode conditionCode, Address address, CiscComputer ciscComputer) {
        ProgramCounter pc = new ProgramCounter();
        pc.setDecimalValue(((conditionCode.getDecimalValue() == 1) && (conditionCode.getConditionCodeType() != null) && (conditionCode.getConditionCodeType().getValue() == firstRegister.getRegisterNumber())) ? address.getEffectiveAddress() : ciscComputer.getProgramCounter().getDecimalValue());
        ciscComputer.setProgramCounter(pc);
        ciscComputer.getConditionCode().setConditionCodeType(null);
        ciscComputer.getConditionCode().setDecimalValue(0);
    }

    private void jumpAddress(Address address, CiscComputer ciscComputer) {
        ProgramCounter pc = new ProgramCounter();
        pc.setDecimalValue(address.getEffectiveAddress());
        ciscComputer.setProgramCounter(pc);
    }

    private void jumpAddressSaveReturnAddress(Address address, CiscComputer ciscComputer) {
        ProgramCounter pc = new ProgramCounter();
        pc.setDecimalValue(address.getEffectiveAddress());

        List<GeneralPurposeRegister> GeneralPurposeRegisters = ciscComputer.getGeneralPurposeRegisters();
        GeneralPurposeRegisters.get(3).setDecimalValue(ciscComputer.getProgramCounter().getDecimalValue());
        GeneralPurposeRegisters.get(0).setBinaryValue(Cache.getWordStringValue(new Address(8)));
//        GeneralPurposeRegisters.get(0).setBinaryValue(String.valueOf(Memory.memoryMap.get(Utils.binaryToDecimal(String.valueOf(Memory.memoryMap.get(8))))));

        ciscComputer.setGeneralPurposeRegisters(GeneralPurposeRegisters);
        ciscComputer.setProgramCounter(pc);
    }

    private void returnFromSubroutine(Address address, CiscComputer ciscComputer) {
        List<GeneralPurposeRegister> GeneralPurposeRegisters = ciscComputer.getGeneralPurposeRegisters();
        ProgramCounter pc = new ProgramCounter();

        pc.setDecimalValue(GeneralPurposeRegisters.get(3).getDecimalValue());
        ciscComputer.setProgramCounter(pc);

        GeneralPurposeRegisters.get(0).setDecimalValue(address.getEffectiveAddress());
        ciscComputer.setGeneralPurposeRegisters(GeneralPurposeRegisters);
    }

    private void subtractOneAndBranch(Register firstRegister, Address address, CiscComputer ciscComputer) {
        ProgramCounter pc = new ProgramCounter();
        firstRegister.setDecimalValue(firstRegister.getDecimalValue() - 1);
        pc.setDecimalValue((firstRegister.getDecimalValue() > 0) ? address.getEffectiveAddress() : ciscComputer.getProgramCounter().getDecimalValue());
        ciscComputer.setProgramCounter(pc);
    }

    private void jumpAddressGreaterOrEqual(Register firstRegister, Address address, CiscComputer ciscComputer) {
        ProgramCounter pc = new ProgramCounter();
        pc.setDecimalValue((firstRegister.getDecimalValue() >= 0) ? address.getEffectiveAddress() : ciscComputer.getProgramCounter().getDecimalValue());
        ciscComputer.setProgramCounter(pc);
    }
}
