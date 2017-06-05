package com.estudio.define.webclient;

import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;

import com.estudio.intf.db.DBParamDataType;
import com.estudio.intf.db.IDBCommand;
import com.estudio.utils.Convert;

public final class SQLParamUtils {
    private SQLParamUtils() {
        super();
    }

    /**
     * 设置参数
     * 
     * @param cmd
     * @param param
     * @param value
     * @throws SQLException
     *             , DBException
     */
    public static void setParam(final IDBCommand cmd, final SQLParam4Portal param, final String paramValue) throws Exception {
        setParam(cmd, param.getDataType(), param.getName(), StringUtils.isEmpty(paramValue) ? param.getInitValue() : paramValue);
    }

    /**
     * 设置参数
     * 
     * @param cmd
     * @param dataType
     * @param paramName
     * @param paramValue
     * @throws SQLException
     *             , DBException
     */
    public static void setParam(final IDBCommand cmd, final DBParamDataType dataType, final String paramName, final String paramValue) throws Exception {
        if (StringUtils.isEmpty(paramValue))
            cmd.setNullParam(paramName);
        else switch (dataType) {
        case String:
        case unknow:
            cmd.setParam(paramName, paramValue);
            break;
        case Int:
            cmd.setParam(paramName, Convert.str2Int(paramValue));
            break;
        case Long:
            cmd.setParam(paramName, Convert.str2Long(paramValue));
            break;
        case Float:
            cmd.setParam(paramName, Convert.str2Float(paramValue));
            break;
        case Double:
            cmd.setParam(paramName, Convert.str2Double(paramValue));
            break;
        case Date:
            cmd.setParam(paramName, Convert.str2Date(paramValue));
            break;
        case DateTime:
        case Timestampe:
            cmd.setParam(paramName, Convert.str2DateTime(paramValue));
            break;
        case Bytes:
            cmd.setParam(paramName, Convert.str2Bytes(paramValue));
            break;
        case Time:
            cmd.setParam(paramName, Convert.str2Time(paramValue));
            break;
        case Decimal:
            cmd.setParam(paramName, Convert.str2Decimal(paramValue));
            break;
        default:
            cmd.setParam(paramName, paramValue);
            break;
        }
    }

}
