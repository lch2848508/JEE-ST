package com.estudio.intf.db;

// �����з���ֵ�洢���̵Ĳ���
public class CallableStmtParamDefineAndValue extends CallableStmtParamDefine {
    public Object value; // ����ֵ

    /**
     * ���캯��
     * 
     * @param value
     *            ����ֵ
     * @param type
     *            ��������
     * @param isOutput
     *            �Ƿ񷵻�ֵ
     */
    public CallableStmtParamDefineAndValue(final Object value, final DBParamDataType type, final boolean isOutput) {
        super();
        this.value = value;
        this.type = type;
        this.isOutput = isOutput;
    }

    /**
     * ���캯��
     * 
     * @param value
     *            ����ֵ
     * @param type
     *            ��������
     */
    public CallableStmtParamDefineAndValue(final Object value, final DBParamDataType type) {
        super();
        this.value = value;
        this.type = type;
    }

}
