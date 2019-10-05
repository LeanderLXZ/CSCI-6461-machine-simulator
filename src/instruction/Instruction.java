package instruction;

import register.GeneralPurposeRegister;
import register.IndexRegister;

/**
 * @author jalal
 * @since 12/9/19
 *
 * This is the class to to represent an instruction
 */
public class Instruction {

    private GeneralPurposeRegister generalPurposeRegister;
    private IndexRegister indexRegister;
    private InstructionType type;
    private int effectiveAddressInDecimal;
    private boolean indirect;

    public Instruction(GeneralPurposeRegister generalPurposeRegister, IndexRegister indexRegister, InstructionType type,
                       int effectiveAddressInDecimal, boolean indirect) {

        this.generalPurposeRegister = generalPurposeRegister;
        this.indexRegister = indexRegister;
        this.type = type;
        this.effectiveAddressInDecimal = effectiveAddressInDecimal;
        this.indirect = indirect;
    }

    public GeneralPurposeRegister getGeneralPurposeRegister() {
        return generalPurposeRegister;
    }

    public IndexRegister getIndexRegister() {
        return indexRegister;
    }

    public InstructionType getType() {
        return type;
    }

    public int getEffectiveAddressInDecimal() {
        return effectiveAddressInDecimal;
    }

    public boolean isIndirect() {
        return indirect;
    }

    public String symbolicForm() {
        String symbolicForm = type.name() + " "
                + (generalPurposeRegister == null ? "" : generalPurposeRegister.getRegisterNumber() + ",")
                + (indexRegister == null ? "0" : indexRegister.getRegisterNumber()) +
                "," + effectiveAddressInDecimal;

        if (indirect) {
            symbolicForm += ",1";
        }

        return symbolicForm;
    }
}