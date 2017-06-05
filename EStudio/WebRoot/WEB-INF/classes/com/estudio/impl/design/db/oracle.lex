object FSynt: TSyntAnalyzer
  Formats = <
    item
      DisplayName = 'Default'
      Font.Charset = ANSI_CHARSET
      Font.Color = clWindowText
      Font.Height = -13
      Font.Name = 'Courier New'
      Font.Style = []
      FormatType = ftCustomFont
    end
    item
      DisplayName = 'Identifier'
      Font.Charset = DEFAULT_CHARSET
      Font.Color = clWindowText
      Font.Height = -13
      Font.Name = 'Courier'
      Font.Style = []
    end
    item
      DisplayName = 'Reserved word'
      Font.Charset = DEFAULT_CHARSET
      Font.Color = clBlue
      Font.Height = -13
      Font.Name = 'Courier'
      Font.Style = []
    end
    item
      DisplayName = 'String'
      Font.Charset = DEFAULT_CHARSET
      Font.Color = clRed
      Font.Height = -13
      Font.Name = 'Courier'
      Font.Style = []
    end
    item
      DisplayName = 'Comment'
      Font.Charset = GB2312_CHARSET
      Font.Color = clGreen
      Font.Height = -12
      Font.Name = 'ProjectStudioFont'
      Font.Style = []
      FormatType = ftCustomFont
    end
    item
      DisplayName = 'Symbol'
      Font.Charset = DEFAULT_CHARSET
      Font.Color = clBlue
      Font.Height = -13
      Font.Name = 'Courier'
      Font.Style = []
    end
    item
      DisplayName = 'Number'
      Font.Charset = DEFAULT_CHARSET
      Font.Color = clMaroon
      Font.Height = -13
      Font.Name = 'Courier'
      Font.Style = []
    end
    item
      DisplayName = 'Marked block'
      Font.Charset = DEFAULT_CHARSET
      Font.Color = clHighlightText
      Font.Height = -13
      Font.Name = 'Courier'
      Font.Style = []
      BgColor = clHighlight
      FormatType = ftColor
    end
    item
      DisplayName = 'Reserved PL/SQL word'
      Font.Charset = DEFAULT_CHARSET
      Font.Color = clBlue
      Font.Height = -13
      Font.Name = 'Courier'
      Font.Style = []
    end
    item
      DisplayName = 'SQL Functions'
      Font.Charset = DEFAULT_CHARSET
      Font.Color = clBlue
      Font.Height = -13
      Font.Name = 'Courier'
      Font.Style = []
    end
    item
      DisplayName = 'SQL*Plus Meta Commands'
      Font.Charset = DEFAULT_CHARSET
      Font.Color = clBlue
      Font.Height = -13
      Font.Name = 'Courier'
      Font.Style = []
    end
    item
      DisplayName = 'Predefined Exceptions'
      Font.Charset = DEFAULT_CHARSET
      Font.Color = clRed
      Font.Height = -13
      Font.Name = 'Courier'
      Font.Style = []
    end
    item
      DisplayName = 'Default Oracle Packages'
      Font.Charset = DEFAULT_CHARSET
      Font.Color = clWindowText
      Font.Height = -13
      Font.Name = 'Courier'
      Font.Style = []
    end
    item
      DisplayName = 'Public Package Procedure'
      Font.Charset = DEFAULT_CHARSET
      Font.Color = clWindowText
      Font.Height = -13
      Font.Name = 'Courier'
      Font.Style = [fsBold, fsItalic]
    end
    item
      DisplayName = 'Function separator'
      Font.Charset = DEFAULT_CHARSET
      Font.Color = clWindowText
      Font.Height = -13
      Font.Name = 'Courier'
      Font.Style = []
      BgColor = clRed
      FormatType = ftBackGround
    end
    item
      DisplayName = 'Current block'
      Font.Charset = DEFAULT_CHARSET
      Font.Color = clBlue
      Font.Height = -13
      Font.Name = 'Courier'
      Font.Style = [fsBold]
      BgColor = clBtnFace
      FormatType = ftColor
      BorderTypeLeft = blDot
      BorderColorLeft = clMaroon
      BorderTypeTop = blDot
      BorderColorTop = clMaroon
      BorderTypeRight = blDot
      BorderColorRight = clMaroon
      BorderTypeBottom = blDot
      BorderColorBottom = clMaroon
    end
    item
      DisplayName = 'Current function'
      Font.Charset = DEFAULT_CHARSET
      Font.Color = clMaroon
      Font.Height = -13
      Font.Name = 'Courier'
      Font.Style = []
      BgColor = 16777205
      FormatType = ftBackGround
      MultiLineBorder = True
    end
    item
      DisplayName = 'Current Line'
      Font.Charset = DEFAULT_CHARSET
      Font.Color = clWindowText
      Font.Height = -13
      Font.Name = 'Courier'
      Font.Style = []
      BgColor = 13369080
      FormatType = ftBackGround
    end
    item
      DisplayName = '()'
      Font.Charset = DEFAULT_CHARSET
      Font.Color = clWindowText
      Font.Height = -13
      Font.Name = 'Courier'
      Font.Style = [fsBold, fsUnderline]
      BgColor = clBtnFace
      BorderTypeLeft = blDot
      BorderTypeTop = blDot
      BorderTypeRight = blDot
      BorderTypeBottom = blDot
    end
    item
      DisplayName = 'Big Bold (tree)'
      Font.Charset = DEFAULT_CHARSET
      Font.Color = clWindowText
      Font.Height = -13
      Font.Name = 'Courier'
      Font.Style = [fsBold]
      FormatType = ftCustomFont
    end
    item
      DisplayName = 'Group (tree)'
      Font.Charset = DEFAULT_CHARSET
      Font.Color = clPurple
      Font.Height = -13
      Font.Name = 'Courier'
      Font.Style = [fsBold, fsItalic]
    end
    item
      DisplayName = 'Function (tree)'
      Font.Charset = DEFAULT_CHARSET
      Font.Color = clNavy
      Font.Height = -13
      Font.Name = 'Courier'
      Font.Style = [fsBold]
    end
    item
      DisplayName = 'Toad_Datatypes'
      Font.Charset = DEFAULT_CHARSET
      Font.Color = clRed
      Font.Height = -13
      Font.Name = 'Courier'
      Font.Style = []
    end
    item
      DisplayName = 'Toad_UserTables'
      Font.Charset = DEFAULT_CHARSET
      Font.Color = clOlive
      Font.Height = -13
      Font.Name = 'Courier'
      Font.Style = []
    end
    item
      DisplayName = 'Toad_UserViews'
      Font.Charset = DEFAULT_CHARSET
      Font.Color = clOlive
      Font.Height = -13
      Font.Name = 'Courier'
      Font.Style = []
    end
    item
      DisplayName = 'Toad_UserProcs'
      Font.Charset = DEFAULT_CHARSET
      Font.Color = clOlive
      Font.Height = -13
      Font.Name = 'Courier'
      Font.Style = []
    end
    item
      DisplayName = 'Toad_SYSViews'
      Font.Charset = DEFAULT_CHARSET
      Font.Color = clOlive
      Font.Height = -13
      Font.Name = 'Courier'
      Font.Style = []
    end>
  TokenRules = <
    item
      DisplayName = 'Comment'
      StyleName = 'Comment'
      TokenType = 1
      Expression = '(?s)(/\*.*?(\*/|\Z))|(?-s)(--.*)|(REM\s.*)'
      ColumnFrom = 0
      ColumnTo = 0
    end
    item
      DisplayName = 'String Q-Quote'
      StyleName = 'String'
      TokenType = 4
      Expression = 
        '(?s)(q'#39')((<.*?)(>'#39'|\Z)|(\(.*?)(\)'#39'|\Z)|({.*?)(}'#39'|\Z)|(\[.*?)(\]'#39 +
        '|\Z))'
      ColumnFrom = 0
      ColumnTo = 0
    end
    item
      DisplayName = 'String Q-Quote Catchall'
      StyleName = 'String'
      TokenType = 4
      Expression = '(?s)(q'#39')((.).*?(\3'#39'|\Z))'
      ColumnFrom = 0
      ColumnTo = 0
    end
    item
      DisplayName = 'String'
      StyleName = 'String'
      TokenType = 4
      Expression = '(?s)('#39'|")(.*?)(\1|\Z)'
      ColumnFrom = 0
      ColumnTo = 0
    end
    item
      DisplayName = 'Float'
      StyleName = 'Number'
      TokenType = 6
      Expression = '\d+\.?\d+e[\+\-]?\d+|\d+\.\d+'
      ColumnFrom = 0
      ColumnTo = 0
    end
    item
      DisplayName = 'Integer'
      StyleName = 'Number'
      TokenType = 5
      Expression = '\d+'
      ColumnFrom = 0
      ColumnTo = 0
    end
    item
      DisplayName = 'Public Package Procedure'
      StyleName = 'Public Package Procedure'
      TokenType = 2
      Expression = 'DBMS_\w+?\.\w+'#13#10
      ColumnFrom = 0
      ColumnTo = 0
    end
    item
      DisplayName = 'Any name'
      StyleName = 'Identifier'
      TokenType = 2
      Expression = '(?r)[\w$\#]+'
      ColumnFrom = 0
      ColumnTo = 0
    end
    item
      DisplayName = 'Symbol'
      StyleName = 'Symbol'
      TokenType = 3
      Expression = '[/,\.;:\(\)=<>\+\-\|]'
      ColumnFrom = 0
      ColumnTo = 0
    end>
  BlockRules = <
    item
      DisplayName = 'Keywords'
      StyleName = 'Reserved word'
      BlockType = btTagDetect
      ConditionList = <
        item
          TagList.Strings = (
            '@'
            '@@'
            'A'
            'ABORT'
            'ABS'
            'ACC'
            'ACCEPT'
            'ACCESS'
            'ACCESS_INTO_NULL'
            'ACCESSED'
            'ACCOUNT'
            'ACOS'
            'ACTIVATE'
            'ADD'
            'ADD_MONTHS'
            'ADMIN'
            'ADMINISTER'
            'ADMINISTRATOR'
            'ADVISE'
            'ADVISOR'
            'AFTER'
            'ALGORITHM'
            'ALIAS'
            'ALL'
            'ALL_ROWS'
            'ALLOCATE'
            'ALLOW'
            'ALTER'
            'ALWAYS'
            'ANALYZE'
            'ANCILLARY'
            'AND'
            'AND_EQUAL'
            'ANTIJOIN'
            'ANY'
            'ANYSCHEMA'
            'APPEND'
            'APPLY'
            'ARCHIVE'
            'ARCHIVELOG'
            'ARRAY'
            'ARRAYLEN'
            'AS'
            'ASC'
            'ASCII'
            'ASIN'
            'ASSOCIATE'
            'AT'
            'ATAN'
            'ATAN2'
            'ATTRIBUTE'
            'ATTRIBUTES'
            'AUDIT'
            'AUTHENTICATED'
            'AUTHENTICATION'
            'AUTHID'
            'AUTHORIZATION'
            'AUTO'
            'AUTOALLOCATE'
            'AUTOEXTEND'
            'AUTOMATIC'
            'AVAILABILITY'
            'AVG'
            'BACKUP'
            'BASE_TABLE'
            'BASICFILE'
            'BATCH'
            'BECOME'
            'BEFORE'
            'BEGIN'
            'BEGIN_OUTLINE_DATA'
            'BEHALF'
            'BETWEEN'
            'BFILE'
            'BFILENAME'
            'BIGFILE'
            'BINARY'
            'BINARY_DOUBLE'
            'BINARY_DOUBLE_INFINITY'
            'BINARY_DOUBLE_NAN'
            'BINARY_FLOAT'
            'BINARY_FLOAT_INFINITY'
            'BINARY_FLOAT_NAN'
            'BINARY_INTEGER'
            'BINDING'
            'BITAND'
            'BITMAP'
            'BITMAP_TREE'
            'BITMAPS'
            'BITS'
            'BLOB'
            'BLOCK'
            'BLOCK_RANGE'
            'BLOCKS'
            'BLOCKSIZE'
            'BODY'
            'BOOLEAN'
            'BOTH'
            'BOUND'
            'BREAK'
            'BROADCAST'
            'BTITLE'
            'BUFFER'
            'BUFFER_CACHE'
            'BUFFER_POOL'
            'BUILD'
            'BULK'
            'BY'
            'BYPASS_RECURSIVE_CHECK'
            'BYPASS_UJVC'
            'BYTE'
            'CACHE'
            'CACHE_CB'
            'CACHE_INSTANCES'
            'CACHE_TEMP_TABLE'
            'CALL'
            'CANCEL'
            'CARDINALITY'
            'CASCADE'
            'CASE'
            'CAST'
            'CATEGORY'
            'CEIL'
            'CELL_FLASH_CACHE'
            'CERTIFICATE'
            'CFILE'
            'CHAINED'
            'CHANGE'
            'CHAR'
            'CHAR_BASE'
            'CHAR_CS'
            'CHARACTER'
            'CHARTOROWID'
            'CHECK'
            'CHECKPOINT'
            'CHILD'
            'CHOOSE'
            'CHR'
            'CHUNK'
            'CIV_GB'
            'CLASS'
            'CLEAR'
            'CLOB'
            'CLONE'
            'CLOSE'
            'CLOSE_CACHED_OPEN_CURSORS'
            'CLUSTER'
            'CLUSTERING_FACTOR'
            'CLUSTERS'
            'COALESCE'
            'COARSE'
            'COLLECT'
            'COLLECTION_IS_NULL'
            'COLUMN'
            'COLUMN_STATS'
            'COLUMN_VALUE'
            'COLUMNS'
            'COMMENT'
            'COMMIT'
            'COMMITTED'
            'COMPACT'
            'COMPATIBILITY'
            'COMPILE'
            'COMPLETE'
            'COMPOSITE_LIMIT'
            'COMPRESS'
            'COMPUTE'
            'CONCAT'
            'CONFORMING'
            'CONNECT'
            'CONNECT_BY_COST_BASED'
            'CONNECT_BY_FILTERING'
            'CONNECT_BY_ISCYCLE'
            'CONNECT_BY_ISLEAF'
            'CONNECT_BY_ROOT'
            'CONNECT_TIME'
            'CONSIDER'
            'CONSISTENT'
            'CONSTANT'
            'CONSTRAINT'
            'CONSTRAINTS'
            'CONTAINER'
            'CONTENT'
            'CONTENTS'
            'CONTEXT'
            'CONTINUE'
            'CONTROLFILE'
            'CONVERT'
            'COPY'
            'CORR_K'
            'CORR_S'
            'CORRUPTION'
            'COS'
            'COSH'
            'COST'
            'COUNT'
            'CPU_COSTING'
            'CPU_PER_CALL'
            'CPU_PER_SESSION'
            'CRASH'
            'CREATE'
            'CREATE_STORED_OUTLINES'
            'CROSS'
            'CROSSEDITION'
            'CUBE'
            'CUBE_GB'
            'CURRENT'
            'CURRENT_DATE'
            'CURRENT_SCHEMA'
            'CURRENT_TIME'
            'CURRENT_TIMESTAMP'
            'CURRENT_USER'
            'CURRVAL'
            'CURSOR'
            'CURSOR_ALREADY_OPEN'
            'CURSOR_SHARING_EXACT'
            'CURSOR_SPECIFIC_SEGMENT'
            'CV'
            'CYCLE'
            'DANGLING'
            'DATA'
            'DATABASE'
            'DATAFILE'
            'DATAFILES'
            'DATAOBJNO'
            'DATE'
            'DATE_MODE'
            'DAY'
            'DB_ROLE_CHANGE'
            'DBA'
            'DBA_RECYCLEBIN'
            'DBMS_STATS'
            'DBTIMEZONE'
            'DDL'
            'DEALLOCATE'
            'DEBUG'
            'DEBUGOFF'
            'DEBUGON'
            'DEC'
            'DECIMAL'
            'DECLARE'
            'DECODE'
            'DECREMENT'
            'DECRYPT'
            'DEDUPLICATE'
            'DEF'
            'DEFAULT'
            'DEFERRABLE'
            'DEFERRED'
            'DEFINE'
            'DEFINE_EDITOR'
            'DEFINED'
            'DEFINER'
            'DEFINITION'
            'DEGREE'
            'DEL'
            'DELAY'
            'DELETE'
            'DELETING'
            'DELTA'
            'DEMAND'
            'DENSE_RANK'
            'DEQUEUE'
            'DEREF'
            'DEREF_NO_REWRITE'
            'DESC'
            'DESCRIBE'
            'DETACHED'
            'DETERMINES'
            'DICTIONARY'
            'DIGITS'
            'DIMENSION'
            'DIRECT_LOAD'
            'DIRECTORY'
            'DISABLE'
            'DISABLE_RPKE'
            'DISALLOW'
            'DISASSOCIATE'
            'DISCONNECT'
            'DISK'
            'DISKGROUP'
            'DISKS'
            'DISMOUNT'
            'DISPATCHERS'
            'DISTINCT'
            'DISTINGUISHED'
            'DISTRIBUTED'
            'DML'
            'DML_UPDATE'
            'DO'
            'DOCUMENT'
            'DOMAIN_INDEX_NO_SORT'
            'DOMAIN_INDEX_SORT'
            'DOUBLE'
            'DOWNGRADE'
            'DRIVING_SITE'
            'DROP'
            'DUMP'
            'DUP_VAL_ON_INDEX'
            'DYNAMIC'
            'DYNAMIC_SAMPLING'
            'DYNAMIC_SAMPLING_EST_CDN'
            'E'
            'EACH'
            'EDIT'
            'EDITION'
            'EDITIONING'
            'EDITIONS'
            'ELEMENT'
            'ELIMINATE_JOIN'
            'ELIMINATE_OBY'
            'ELIMINATE_OUTER_JOIN'
            'ELSE'
            'ELSIF'
            'EMPTY'
            'EMPTY_BLOB'
            'EMPTY_CLOB'
            'ENABLE'
            'ENCRYPT'
            'ENCRYPTED'
            'ENCRYPTION'
            'END'
            'END_OUTLINE_DATA'
            'ENFORCE'
            'ENFORCED'
            'ENQUEUE'
            'ENTERPRISE'
            'ENTRY'
            'ERROR'
            'ERROR_ON_OVERLAP_TIME'
            'ERRORS'
            'ESCAPE'
            'ESTIMATE'
            'EVALNAME'
            'EVALUATION'
            'EVENTS'
            'EXCEPT'
            'EXCEPTION'
            'EXCEPTION_INIT'
            'EXCEPTIONS'
            'EXCHANGE'
            'EXCLUDING'
            'EXCLUSIVE'
            'EXEC'
            'EXECUTE'
            'EXEMPT'
            'EXISTS'
            'EXIT'
            'EXP'
            'EXPAND_GSET_TO_UNION'
            'EXPIRE'
            'EXPLAIN'
            'EXPLOSION'
            'EXPORT'
            'EXPR_CORR_CHECK'
            'EXTEND'
            'EXTENDS'
            'EXTENT'
            'EXTENTS'
            'EXTERNAL'
            'EXTERNALLY'
            'EXTRACT'
            'FACT'
            'FAILED'
            'FAILED_LOGIN_ATTEMPTS'
            'FAILGROUP'
            'FALSE'
            'FAST'
            'FBTSCAN'
            'FETCH'
            'FIC_CIV'
            'FIC_PIV'
            'FILE'
            'FILESYSTEM_LIKE_LOGGING'
            'FILTER'
            'FINAL'
            'FINE'
            'FINISH'
            'FIRST'
            'FIRST_ROWS'
            'FLAGGER'
            'FLASH_CACHE'
            'FLASHBACK'
            'FLOAT'
            'FLOB'
            'FLOOR'
            'FLUSH'
            'FOLLOWING'
            'FOR'
            'FORALL'
            'FORCE'
            'FORCE_XML_QUERY_REWRITE'
            'FOREIGN'
            'FORM'
            'FORTRAN'
            'FORWARD'
            'FOUND'
            'FREELIST'
            'FREELISTS'
            'FREEPOOLS'
            'FRESH'
            'FROM'
            'FULL'
            'FUNCTION'
            'FUNCTIONS'
            'G'
            'GATHER_PLAN_STATISTICS'
            'GBY_CONC_ROLLUP'
            'GENERATED'
            'GENERIC'
            'GET'
            'GLOBAL'
            'GLOBAL_NAME'
            'GLOBAL_TOPIC_ENABLED'
            'GLOBALLY'
            'GO'
            'GOTO'
            'GRANT'
            'GREATEST'
            'GROUP'
            'GROUP_BY'
            'GROUPING'
            'GROUPS'
            'GUARANTEE'
            'GUARANTEED'
            'GUARD'
            'HASH'
            'HASH_AJ'
            'HASH_SJ'
            'HASHKEYS'
            'HAVING'
            'HEADER'
            'HEAP'
            'HELP'
            'HEXTORAW'
            'HIERARCHY'
            'HIGH'
            'HINTSET_BEGIN'
            'HINTSET_END'
            'HOST'
            'HOUR'
            'HWM_BROKERED'
            'ID'
            'IDENTIFIED'
            'IDENTIFIER'
            'IDENTITY'
            'IDGENERATORS'
            'IDLE_TIME'
            'IF'
            'IGNORE'
            'IGNORE NAV'
            'IGNORE_OPTIM_EMBEDDED_HINTS'
            'IGNORE_WHERE_CLAUSE'
            'IMMEDIATE'
            'IMPORT'
            'IN'
            'IN_MEMORY_METADATA'
            'INCLUDE_VERSION'
            'INCLUDING'
            'INCREMENT'
            'INCREMENTAL'
            'INDEX'
            'INDEX_ASC'
            'INDEX_COMBINE'
            'INDEX_DESC'
            'INDEX_FFS'
            'INDEX_FILTER'
            'INDEX_JOIN'
            'INDEX_ROWS'
            'INDEX_RRS'
            'INDEX_SCAN'
            'INDEX_SKIP_SCAN'
            'INDEX_SS'
            'INDEX_SS_ASC'
            'INDEX_SS_DESC'
            'INDEX_STATS'
            'INDEXED'
            'INDEXES'
            'INDEXTYPE'
            'INDEXTYPES'
            'INDICATOR'
            'INFINITE'
            'INFORMATIONAL'
            'INITCAP'
            'INITIAL'
            'INITIALIZED'
            'INITIALLY'
            'INITRANS'
            'INLINE'
            'INLINE_XMLTYPE_NT'
            'INNER'
            'INPUT'
            'INSERT'
            'INSERTING'
            'INSTANCE'
            'INSTANCES'
            'INSTANTIABLE'
            'INSTANTLY'
            'INSTEAD'
            'INSTR'
            'INSTRB'
            'INT'
            'INTEGER'
            'INTEGRITY'
            'INTERFACE'
            'INTERMEDIATE'
            'INTERNAL_CONVERT'
            'INTERNAL_USE'
            'INTERPRETED'
            'INTERSECT'
            'INTERVAL'
            'INTO'
            'INVALID_CURSOR'
            'INVALID_NUMBER'
            'INVALIDATE'
            'INVISIBLE'
            'IS'
            'IS A SET'
            'IS ANY'
            'IS EMPTY'
            'IS PRESENT'
            'ISOLATION'
            'ISOLATION_LEVEL'
            'ITERATE'
            'ITERATION_NUMBER'
            'JAVA'
            'JOB'
            'JOIN'
            'K'
            'KEEP'
            'KEEP_DUPLICATES'
            'KERBEROS'
            'KEY'
            'KEY_LENGTH'
            'KEYFILE'
            'KEYS'
            'KEYSIZE'
            'KILL'
            'LABEL'
            'LANGUAGE'
            'LAST'
            'LAST_DAY'
            'LATERAL'
            'LAYER'
            'LDAP_REG_SYNC_INTERVAL'
            'LDAP_REGISTRATION'
            'LDAP_REGISTRATION_ENABLED'
            'LEADING'
            'LEAST'
            'LEFT'
            'LENGTH'
            'LENGTHB'
            'LESS'
            'LEVEL'
            'LEVELS'
            'LIBRARY'
            'LIKE'
            'LIKE_EXPAND'
            'LIKE2'
            'LIKE4'
            'LIKEC'
            'LIMIT'
            'LIMITED'
            'LINK'
            'LIST'
            'LISTS'
            'LN'
            'LOB'
            'LOCAL'
            'LOCAL_INDEXES'
            'LOCALTIME'
            'LOCALTIMESTAMP'
            'LOCATION'
            'LOCATOR'
            'LOCK'
            'LOCKED'
            'LOG'
            'LOGFILE'
            'LOGGING'
            'LOGICAL'
            'LOGICAL_READS_PER_CALL'
            'LOGICAL_READS_PER_SESSION'
            'LOGIN_DENIED'
            'LOGOFF'
            'LOGON'
            'LONG'
            'LOOP'
            'LOWER'
            'LPAD'
            'LTRIM'
            'M'
            'MAIN'
            'MAKE_REF'
            'MANAGE'
            'MANAGED'
            'MANAGEMENT'
            'MANUAL'
            'MAPPING'
            'MASTER'
            'MATCHED'
            'MATERIALIZE'
            'MATERIALIZED'
            'MAX'
            'MAXARCHLOGS'
            'MAXDATAFILES'
            'MAXEXTENTS'
            'MAXIMIZE'
            'MAXINSTANCES'
            'MAXLOGFILES'
            'MAXLOGHISTORY'
            'MAXLOGMEMBERS'
            'MAXSIZE'
            'MAXTRANS'
            'MAXVALUE'
            'MEASURES'
            'MEDIAN'
            'MEDIUM'
            'MEMBER'
            'MEMORY'
            'MERGE'
            'MERGE_AJ'
            'MERGE_CONST_ON'
            'MERGE_SJ'
            'METHOD'
            'MIGRATE'
            'MIN'
            'MINEXTENTS'
            'MINIMIZE'
            'MINIMUM'
            'MINUS'
            'MINUS_NULL'
            'MINUTE'
            'MINVALUE'
            'MIRROR'
            'MLSLABEL'
            'MOD'
            'MODE'
            'MODEL'
            'MODEL_COMPILE_SUBQUERY'
            'MODEL_DONTVERIFY_UNIQUENESS'
            'MODEL_DYNAMIC_SUBQUERY'
            'MODEL_MIN_ANALYSIS'
            'MODEL_NO_ANALYSIS'
            'MODEL_PBY'
            'MODEL_PUSH_REF'
            'MODIFY'
            'MONITORING'
            'MONTH'
            'MONTHS_BETWEEN'
            'MOUNT'
            'MOVE'
            'MOVEMENT'
            'MTS_DISPATCHERS'
            'MULTISET'
            'MV_MERGE'
            'NAME'
            'NAMED'
            'NAN'
            'NANVL'
            'NATIONAL'
            'NATIVE'
            'NATURAL'
            'NATURALN'
            'NAV'
            'NCHAR'
            'NCHAR_CS'
            'NCLOB'
            'NEEDED'
            'NESTED'
            'NESTED_TABLE_FAST_INSERT'
            'NESTED_TABLE_GET_REFS'
            'NESTED_TABLE_ID'
            'NESTED_TABLE_SET_REFS'
            'NESTED_TABLE_SET_SETID'
            'NETWORK'
            'NEVER'
            'NEW'
            'NEW_TIME'
            'NEXT'
            'NEXT_DAY'
            'NEXTVAL'
            'NL_AJ'
            'NL_SJ'
            'NLS_CALENDAR'
            'NLS_CHARACTERSET'
            'NLS_CHARSET_DECL_LEN'
            'NLS_CHARSET_ID'
            'NLS_CHARSET_NAME'
            'NLS_COMP'
            'NLS_CURRENCY'
            'NLS_DATE_FORMAT'
            'NLS_DATE_LANGUAGE'
            'NLS_INITCAP'
            'NLS_ISO_CURRENCY'
            'NLS_LANG'
            'NLS_LANGUAGE'
            'NLS_LENGTH_SEMANTICS'
            'NLS_LOWER'
            'NLS_NCHAR_CONV_EXCP'
            'NLS_NUMERIC_CHARACTERS'
            'NLS_SORT'
            'NLS_SPECIAL_CHARS'
            'NLS_TERRITORY'
            'NLS_UPPER'
            'NLSSORT'
            'NO'
            'NO_ACCESS'
            'NO_BASETABLE_MULTIMV_REWRITE'
            'NO_BUFFER'
            'NO_CARTESIAN'
            'NO_CONNECT_BY_COST_BASED'
            'NO_CONNECT_BY_FILTERING'
            'NO_CPU_COSTING'
            'NO_DATA_FOUND'
            'NO_ELIMINATE_JOIN'
            'NO_ELIMINATE_OBY'
            'NO_ELIMINATE_OUTER_JOIN'
            'NO_EXPAND'
            'NO_EXPAND_GSET_TO_UNION'
            'NO_FACT'
            'NO_FILTERING'
            'NO_INDEX'
            'NO_INDEX_FFS'
            'NO_INDEX_SS'
            'NO_MERGE'
            'NO_MODEL_PUSH_REF'
            'NO_MONITORING'
            'NO_MULTIMV_REWRITE'
            'NO_ORDER_ROLLUPS'
            'NO_PARALLEL'
            'NO_PARALLEL_INDEX'
            'NO_PARTIAL_COMMIT'
            'NO_PRUNE_GSETS'
            'NO_PULL_PRED'
            'NO_PUSH_PRED'
            'NO_PUSH_SUBQ'
            'NO_PX_JOIN_FILTER'
            'NO_QKN_BUFF'
            'NO_QUERY_TRANSFORMATION'
            'NO_REF_CASCADE'
            'NO_REWRITE'
            'NO_SEMIJOIN'
            'NO_SET_TO_JOIN'
            'NO_SQL_TUNE'
            'NO_STAR_TRANSFORMATION'
            'NO_STATS_GSETS'
            'NO_SWAP_JOIN_INPUTS'
            'NO_TEMP_TABLE'
            'NO_UNNEST'
            'NO_USE_HASH'
            'NO_USE_HASH_AGGREGATION'
            'NO_USE_MERGE'
            'NO_USE_NL'
            'NO_XML_QUERY_REWRITE'
            'NOAPPEND'
            'NOARCHIVELOG'
            'NOAUDIT'
            'NOCACHE'
            'NOCOMPRESS'
            'NOCOPY'
            'NOCPU_COSTING'
            'NOCYCLE'
            'NODELAY'
            'NOFORCE'
            'NOGUARANTEE'
            'NOLOGGING'
            'NOMAPPING'
            'NOMAXVALUE'
            'NOMINIMIZE'
            'NOMINVALUE'
            'NOMONITORING'
            'NOMOUNT'
            'NONE'
            'NONSCHEMA'
            'NOORDER'
            'NOOVERRIDE'
            'NOPARALLEL'
            'NOPARALLEL_INDEX'
            'NORELY'
            'NOREPAIR'
            'NORESETLOGS'
            'NOREVERSE'
            'NOREWRITE'
            'NORMAL'
            'NOROWDEPENDENCIES'
            'NOSEGMENT'
            'NOSORT'
            'NOSTRICT'
            'NOSTRIPE'
            'NOSWITCH'
            'NOT'
            'NOT_LOGGED_ON'
            'NOTFOUND'
            'NOTHING'
            'NOTIFICATION'
            'NOVALIDATE'
            'NOWAIT'
            'NULL'
            'NULLIF'
            'NULLS'
            'NUMBER'
            'NUMBER_BASE'
            'NUMERIC'
            'NVARCHAR'
            'NVARCHAR2'
            'NVL'
            'OBJECT'
            'OBJNO'
            'OBJNO_REUSE'
            'OCIROWID'
            'OF'
            'OFF'
            'OFFLINE'
            'OID'
            'OIDINDEX'
            'OLD'
            'OLD_PUSH_PRED'
            'ON'
            'ONLINE'
            'ONLY'
            'OPAQUE'
            'OPAQUE_TRANSFORM'
            'OPAQUE_XCANONICAL'
            'OPCODE'
            'OPEN'
            'OPERATIONS'
            'OPERATOR'
            'OPT_ESTIMATE'
            'OPT_PARAM'
            'OPTIMAL'
            'OPTIMIZER_FEATURES_ENABLE'
            'OPTIMIZER_GOAL'
            'OPTION'
            'OR'
            'OR_EXPAND'
            'ORA_HASH'
            'ORA_ROWSCN'
            'ORDER'
            'ORDERED'
            'ORDERED_PREDICATES'
            'ORDINALITY'
            'ORGANIZATION'
            'OSERROR'
            'OTHERS'
            'OUT'
            'OUT_OF_LINE'
            'OUTER'
            'OUTLINE'
            'OUTLINE_LEAF'
            'OVER'
            'OVERFLOW'
            'OVERFLOW_NOMOVE'
            'OVERLAPS'
            'OWN'
            'P'
            'PACKAGE'
            'PACKAGES'
            'PARALLEL'
            'PARALLEL_INDEX'
            'PARAMETERS'
            'PARENT'
            'PARITY'
            'PARTIALLY'
            'PARTITION'
            'PARTITION_HASH'
            'PARTITION_LIST'
            'PARTITION_RANGE'
            'PARTITIONS'
            'PASSING'
            'PASSWORD'
            'PASSWORD_GRACE_TIME'
            'PASSWORD_LIFE_TIME'
            'PASSWORD_LOCK_TIME'
            'PASSWORD_REUSE_MAX'
            'PASSWORD_REUSE_TIME'
            'PASSWORD_VERIFY_FUNCTION'
            'PATH'
            'PATHS'
            'PAUSE'
            'PCTFREE'
            'PCTINCREASE'
            'PCTTHRESHOLD'
            'PCTUSED'
            'PCTVERSION'
            'PERCENT'
            'PERFORMANCE'
            'PERMANENT'
            'PFILE'
            'PHYSICAL'
            'PIV_GB'
            'PIV_SSF'
            'PLAN'
            'PLI'
            'PLS_INTEGER'
            'PLSQL_CCFLAGS'
            'PLSQL_CODE_TYPE'
            'PLSQL_DEBUG'
            'PLSQL_OPTIMIZE_LEVEL'
            'PLSQL_WARNINGS'
            'POINT'
            'POLICY'
            'POSITIVE'
            'POSITIVEN'
            'POST_TRANSACTION'
            'POWER'
            'POWERMULTISET'
            'POWERMULTISET_BY_CARDINALITY'
            'PQ_DISTRIBUTE'
            'PQ_MAP'
            'PQ_NOMAP'
            'PRAGMA'
            'PREBUILT'
            'PRECEDING'
            'PRECISION'
            'PRECOMPUTE_SUBQUERY'
            'PREPARE'
            'PRESENT'
            'PRESENTNNV'
            'PRESENTV'
            'PRESERVE'
            'PRESERVE_OID'
            'PREVIOUS'
            'PRIMARY'
            'PRINT'
            'PRIOR'
            'PRIVATE'
            'PRIVATE_SGA'
            'PRIVILEGE'
            'PRIVILEGES'
            'PROCEDURE'
            'PROFILE'
            'PROGRAM'
            'PROGRAM_ERROR'
            'PROJECT'
            'PROMPT'
            'PROTECTED'
            'PROTECTION'
            'PUBLIC'
            'PULL_PRED'
            'PURGE'
            'PUSH_PRED'
            'PUSH_SUBQ'
            'PX_GRANULE'
            'PX_JOIN_FILTER'
            'QB_NAME'
            'QUERY'
            'QUERY_BLOCK'
            'QUEUE'
            'QUEUE_CURR'
            'QUEUE_ROWP'
            'QUIESCE'
            'QUOTA'
            'RAISE'
            'RAISE_APPLICATION_ERROR'
            'RANDOM'
            'RANGE'
            'RAPIDLY'
            'RAW'
            'RAWTOHEX'
            'RBA'
            'RBO_OUTLINE'
            'READ'
            'READS'
            'REAL'
            'REBALANCE'
            'REBUILD'
            'RECORD'
            'RECORDS_PER_BLOCK'
            'RECOVER'
            'RECOVERABLE'
            'RECOVERY'
            'RECYCLE'
            'RECYCLEBIN'
            'REDUCED'
            'REDUNDANCY'
            'REF'
            'REF_CASCADE_CURSOR'
            'REFERENCE'
            'REFERENCED'
            'REFERENCES'
            'REFERENCING'
            'REFRESH'
            'REFTOHEX'
            'REGEXP_INSTR'
            'REGEXP_LIKE'
            'REGEXP_REPLACE'
            'REGEXP_SUBSTR'
            'REGISTER'
            'REJECT'
            'REKEY'
            'RELATIONAL'
            'release'
            'RELY'
            'rem'
            'REMAINDER'
            'REMARK'
            'REMOTE_MAPPED'
            'RENAME'
            'REPAIR'
            'REPLACE'
            'REQUIRED'
            'RESET'
            'RESETLOGS'
            'RESIZE'
            'RESOLVE'
            'RESOLVER'
            'RESOURCE'
            'RESTORE'
            'RESTORE_AS_INTERVALS'
            'RESTRICT'
            'RESTRICT_ALL_REF_CONS'
            'RESTRICT_REFERENCES'
            'RESTRICTED'
            'RESULT_CACHE'
            'RESUMABLE'
            'RESUME'
            'RETENTION'
            'RETURN'
            'RETURNING'
            'REUSE'
            'REVERSE'
            'REVOKE'
            'REWRITE'
            'REWRITE_OR_ERROR'
            'RIGHT'
            'rnds'
            'rnps'
            'ROLE'
            'ROLES'
            'ROLLBACK'
            'ROLLING'
            'ROLLUP'
            'ROUND'
            'ROW'
            'ROW_LENGTH'
            'rowcount'
            'ROWDEPENDENCIES'
            'ROWID'
            'ROWIDTOCHAR'
            'ROWLABEL'
            'ROWNUM'
            'ROWS'
            'ROWTYPE'
            'RPAD'
            'RTRIM'
            'RULE'
            'RULES'
            'RUN'
            'RUNFORM'
            'SALT'
            'SAMPLE'
            'SAVE'
            'SAVE_AS_INTERVALS'
            'SAVEPOINT'
            'SB4'
            'SCALE'
            'SCALE_ROWS'
            'SCAN'
            'SCAN_INSTANCES'
            'SCHEDULER'
            'SCHEMA'
            'SCN'
            'SCN_ASCENDING'
            'SCN_TO_TIMESTAMP'
            'SCOPE'
            'SD_ALL'
            'SD_INHIBIT'
            'SD_SHOW'
            'SECOND'
            'SECTION'
            'SECUREFILE'
            'SECURITY'
            'SEED'
            'SEG_BLOCK'
            'SEG_FILE'
            'SEGMENT'
            'SELECT'
            'SELECTIVITY'
            'SEMIJOIN'
            'SEMIJOIN_DRIVER'
            'SEPARATE'
            'SEQUENCE'
            'SEQUENCED'
            'SEQUENTIAL'
            'SEQUENTUAL'
            'SERIALIZABLE'
            'SERVERERROR'
            'SESSION'
            'SESSION_CACHED_CURSORS'
            'SESSIONS_PER_USER'
            'SESSIONTIMEZONE'
            'SESSIONTZNAME'
            'SET'
            'SET_TO_JOIN'
            'SET_TRANSACTION_USE'
            'SETS'
            'SETTINGS'
            'SEVERE'
            'SHARE'
            'SHARED'
            'SHARED_POOL'
            'SHOW'
            'SHRINK'
            'SHUTDOWN'
            'SIBLINGS'
            'SID'
            'SIGN'
            'SIMPLE'
            'SIN'
            'SINGLE'
            'SINGLE REFERENCE'
            'SINGLETASK'
            'SINH'
            'SIZE'
            'SKIP'
            'SKIP_EXT_OPTIMIZER'
            'SKIP_UNQ_UNUSABLE_IDX'
            'SKIP_UNUSABLE_INDEXES'
            'SMALLFILE'
            'SMALLINT'
            'SNAPSHOT'
            'SOME'
            'SORT'
            'SOUNDEX'
            'SOURCE'
            'SPACE'
            'SPECIFICATION'
            'SPFILE'
            'SPLIT'
            'spo'
            'SPOOL'
            'SPREADSHEET'
            'SQL'
            'SQL_TRACE'
            'SQLBUF'
            'SQLCODE'
            'SQLERRM'
            'SQLERROR'
            'SQLLDR'
            'SQLPLUS'
            'SQLSTATE'
            'SQRT'
            'STA'
            'STANDALONE'
            'STANDBY'
            'STAR'
            'STAR_TRANSFORMATION'
            'START'
            'STARTUP'
            'STATEMENT'
            'STATEMENT_ID'
            'STATIC'
            'STATISTICS'
            'STATS_BINOMIAL_TEST'
            'STATS_CROSSTAB'
            'STATS_F_TEST'
            'STATS_KS_TEST'
            'STATS_MODE'
            'STATS_MW_TEST'
            'STATS_ONE_WAY_ANOVA'
            'STATS_T_TEST_INDEP'
            'STATS_T_TEST_INDEPU'
            'STATS_T_TEST_ONE'
            'STATS_T_TEST_PAIRED'
            'STATS_WSR_TEST'
            'STDDEV'
            'STOP'
            'STORAGE'
            'STORAGE_ERROR'
            'STORE'
            'STREAMS'
            'STRICT'
            'STRING'
            'STRIP'
            'STRIPE'
            'STRUCTURE'
            'SUBMULTISET'
            'SUBPARTITION'
            'SUBPARTITION_REL'
            'SUBPARTITIONS'
            'SUBQUERIES'
            'SUBSCRIPT_BEYOND_COUNT'
            'SUBSCRIPT_OUTSIDE_LIMIT'
            'SUBSTITUTABLE'
            'SUBSTR'
            'SUBSTRB'
            'SUBTYPE'
            'SUCCESSFUL'
            'SUM'
            'SUMMARY'
            'SUPPLEMENTAL'
            'SUSPEND'
            'SWAP_JOIN_INPUTS'
            'SWITCH'
            'SWITCHOVER'
            'SYNONYM'
            'SYS_CONNECT_BY_PATH'
            'SYS_DL_CURSOR'
            'SYS_FBT_INSDEL'
            'SYS_OP_BITVEC'
            'SYS_OP_CAST'
            'SYS_OP_COL_PRESENT'
            'SYS_OP_ENFORCE_NOT_NULL$'
            'SYS_OP_EXTRACT'
            'SYS_OP_MINE_VALUE'
            'SYS_OP_NOEXPAND'
            'SYS_OP_NTCIMG$'
            'SYS_PARALLEL_TXN'
            'SYS_RID_ORDER'
            'SYS_XMLAGG'
            'SYS_XMLGEN'
            'SYSAUX'
            'SYSDATE'
            'SYSDBA'
            'SYSOPER'
            'SYSTEM'
            'SYSTIMESTAMP'
            'T'
            'TABAUTH'
            'TABLE'
            'TABLE_STATS'
            'TABLES'
            'TABLESPACE'
            'TABLESPACE_NO'
            'TABNO'
            'TAN'
            'TANH'
            'TASK'
            'TEMP_TABLE'
            'TEMPFILE'
            'TEMPLATE'
            'TEMPORARY'
            'TERMINATE'
            'TEST'
            'THAN'
            'THE'
            'THEN'
            'THREAD'
            'THROUGH'
            'TIME'
            'TIME_ZONE'
            'TIMEOUT'
            'TIMEOUT_ON_RESOURCE'
            'TIMESTAMP'
            'TIMEZONE_ABBR'
            'TIMEZONE_HOUR'
            'TIMEZONE_MINUTE'
            'TIMEZONE_OFFSET'
            'TIMEZONE_REGION'
            'TIMING'
            'TIV_GB'
            'TIV_SSF'
            'TO'
            'TO_BINARY_DOUBLE'
            'TO_BINARY_FLOAT'
            'TO_CHAR'
            'TO_DATE'
            'TO_MULTI_BYTE'
            'TO_NUMBER'
            'TO_SINGLE_BYTE'
            'TOO_MANY_ROWS'
            'TOPLEVEL'
            'TRACE'
            'TRACING'
            'TRACKING'
            'TRAILING'
            'TRANSACTION'
            'TRANSACTIONAL'
            'TRANSITIONAL'
            'TRANSLATE'
            'TREAT'
            'TRIGGER'
            'TRIGGERS'
            'TRUE'
            'TRUNC'
            'TRUNCATE'
            'TRUSTED'
            'TTITLE'
            'TUNING'
            'TX'
            'TYPE'
            'TYPES'
            'TZ_OFFSET'
            'U'
            'UB2'
            'UBA'
            'UI'
            'UID'
            'UNARCHIVED'
            'UNBOUND'
            'UNBOUNDED'
            'UNDEF'
            'UNDEFINE'
            'UNDER'
            'UNDO'
            'UNDROP'
            'UNIFORM'
            'UNION'
            'UNIQUE'
            'UNLIMITED'
            'UNLOCK'
            'UNNEST'
            'UNPACKED'
            'UNPROTECTED'
            'UNQUIESCE'
            'UNRECOVERABLE'
            'UNTIL'
            'UNUSABLE'
            'UNUSED'
            'UPD_INDEXES'
            'UPD_JOININDEX'
            'UPDATABLE'
            'UPDATE'
            'UPDATED'
            'UPDATING'
            'UPGRADE'
            'UPPER'
            'UPSERT'
            'UROWID'
            'USAGE'
            'USE'
            'USE_ANTI'
            'USE_CONCAT'
            'USE_HASH'
            'USE_HASH_AGGREGATION'
            'USE_MERGE'
            'USE_NL'
            'USE_NL_WITH_INDEX'
            'USE_PRIVATE_OUTLINES'
            'USE_SEMI'
            'USE_STORED_OUTLINES'
            'USE_TTT_FOR_GSETS'
            'USE_WEAK_NAME_RESL'
            'USER'
            'USER_DEFINED'
            'USER_RECYCLEBIN'
            'USERENV'
            'USERS'
            'USING'
            'VALIDATE'
            'VALIDATION'
            'VALUE'
            'VALUE_ERROR'
            'VALUES'
            'VARCHAR'
            'VARCHAR2'
            'VARIABLE'
            'VARIANCE'
            'VARRAY'
            'VARYING'
            'VECTOR_READ'
            'VECTOR_READ_TRACE'
            'VERSION'
            'VERSIONS'
            'VIEW'
            'VIEWS'
            'VISIBLE'
            'VSIZE'
            'WAIT'
            'WALLET'
            'WELLFORMED'
            'WHEN'
            'WHENEVER'
            'WHERE'
            'WHILE'
            'WHITESPACE'
            'WITH'
            'WITHIN'
            'WITHOUT'
            'WNDS'
            'WNPS'
            'WORK'
            'WRAPPED'
            'WRITE'
            'X_DYN_PRUNE'
            'XID'
            'XML'
            'XMLATTRIBUTES'
            'XMLCOLATTVAL'
            'XMLELEMENT'
            'XMLFOREST'
            'XMLNAMESPACES'
            'XMLPARSE'
            'XMLPI'
            'XMLQUERY'
            'XMLROOT'
            'XMLSCHEMA'
            'XMLSERIALIZE'
            'XMLTABLE'
            'XMLTYPE'
            'YEAR'
            'YES'
            'ZERO_DIVIDE'
            'ZONE')
          TokenTypes = 4
          IgnoreCase = True
        end>
      HighlightPos = cpAny
      IgnoreAsParent = False
    end
    item
      DisplayName = 'SQL*Plus Commands'
      StyleName = 'SQL*Plus Meta Commands'
      BlockType = btTagDetect
      ConditionList = <
        item
          TagList.Strings = (
            '@'
            '@@'
            'ABORT'
            'ACC'
            'ACCEPT'
            'APPEND'
            'BREAK'
            'BTITLE'
            'CHANGE'
            'CLEAR'
            'COLUMN'
            'COMPUTE'
            'CONNECT'
            'COPY'
            'DEF'
            'DEFINE'
            'DEFINE_EDITOR'
            'DEL'
            'DESC'
            'DESCRIBE'
            'DISCONNECT'
            'EDIT'
            'EXEC'
            'EXECUTE'
            'EXIT'
            'GET'
            'HELP'
            'HOST'
            'IMMEDIATE'
            'INPUT'
            'LIST'
            'MOUNT'
            'NOMOUNT'
            'NORMAL'
            'OFF'
            'OSERROR'
            'PAUSE'
            'PRINT'
            'PROMPT'
            'REM'
            'REMARK'
            'RUN'
            'RUNFORM'
            'SAVE'
            'SET'
            'SHOW'
            'SHUTDOWN'
            'SPO'
            'SPOOL'
            'SQLERROR'
            'SQLPLUS'
            'STA'
            'START'
            'STARTUP'
            'TIMING'
            'TRANSACTIONAL'
            'TTITLE'
            'UNDEF'
            'UNDEFINE'
            'VARIABLE'
            'WHENEVER')
          TokenTypes = 4
          IgnoreCase = True
        end>
      HighlightPos = cpAny
      IgnoreAsParent = False
    end
    item
      DisplayName = 'SQL Functions'
      StyleName = 'SQL Functions'
      BlockType = btTagDetect
      ConditionList = <
        item
          TagList.Strings = (
            'abs'
            'acos'
            'add_months'
            'ascii'
            'asciistr'
            'asin'
            'atan'
            'atan2'
            'avg'
            'bfilename'
            'bin_to_num'
            'bitand'
            'cast'
            'ceil'
            'chartorowid'
            'chr'
            'coalesce'
            'compose'
            'concat'
            'convert'
            'corr'
            'cos'
            'cosh'
            'count'
            'covar_pop'
            'covar_samp'
            'cume_dist'
            'current_date'
            'current_timestamp'
            'dbtimezone'
            'decode'
            'decompose'
            'dense_rank'
            'depth'
            'deref'
            'dump'
            'empty_blob'
            'empty_clob'
            'existsnode'
            'exp'
            'extract'
            'extractvalue'
            'first'
            'floor'
            'from_tz'
            'greatest'
            'group_id'
            'grouping'
            'grouping_id'
            'hextoraw'
            'initcap'
            'instr'
            'last'
            'last_day'
            'least'
            'length'
            'ln'
            'localtimestamp'
            'log'
            'lower'
            'lpad'
            'ltrim'
            'make_ref'
            'max'
            'min'
            'mod'
            'months_between'
            'new_time'
            'next_day'
            'nls_charset_decl_len'
            'nls_charset_id'
            'nls_charset_name'
            'nls_initcap'
            'nls_lower'
            'nls_upper'
            'nlssort'
            'nullif'
            'numtodsinterval'
            'numtoyminterval'
            'nvl'
            'nvl2'
            'path'
            'percent_rank'
            'percentile_cont'
            'percentile_disc'
            'power'
            'rank'
            'rawtohex'
            'rawtonhex'
            'ref'
            'reftohex'
            'regr_avgx'
            'regr_avgy'
            'regr_count'
            'regr_intercept'
            'regr_r2'
            'regr_slope'
            'regr_sxx'
            'regr_sxystddev'
            'regr_syy'
            'round'
            'rowidtochar'
            'rowidtonchar'
            'rpad'
            'rtrim'
            'sessiontimezone'
            'sign'
            'sin'
            'sinh'
            'soundex'
            'sqrt'
            'stddev'
            'stddev_pop'
            'stddev_samp'
            'substr'
            'sum'
            'sys_connect_by_path'
            'sys_context'
            'sys_dburigen'
            'sys_extract_utc'
            'sys_guid'
            'sys_typeid'
            'sys_xmlagg'
            'sys_xmlgen'
            'sysdate'
            'systimestamp'
            'tan'
            'tanh'
            'to_char'
            'to_clob'
            'to_date'
            'to_dsinterval'
            'to_lob'
            'to_multi_byte'
            'to_nchar'
            'to_nclob'
            'to_number'
            'to_single_byte'
            'to_timestamp'
            'to_timestamp_tz'
            'to_yminterval'
            'translate'
            'treat'
            'trim'
            'trunc'
            'tz_offset'
            'uid'
            'unistr'
            'updatexml'
            'upper'
            'user'
            'userenv'
            'using'
            'value'
            'var_pop'
            'var_samp'
            'variance'
            'vsize'
            'width_bucket'
            'xmlagg'
            'xmlcolattval'
            'xmlconcat'
            'xmlforest'
            'xmlsequence'
            'xmltransform')
          TokenTypes = 4
          IgnoreCase = True
        end>
      HighlightPos = cpAny
      IgnoreAsParent = False
    end
    item
      DisplayName = 'Default Oracle Packages'
      StyleName = 'Default Oracle Packages'
      BlockType = btTagDetect
      ConditionList = <
        item
          TagList.Strings = (
            'dbms_alert'
            'dbms_application_info'
            'dbms_aq'
            'dbms_aqadm'
            'dbms_aqelm'
            'dbms_backup_restore'
            'dbms_ddl'
            'dbms_debug'
            'dbms_defer'
            'dbms_defer_query'
            'dbms_defer_sys'
            'dbms_describe'
            'dbms_distributed_trust_admin'
            'dbms_fga'
            'dbms_flashback'
            'dbms_hs_passthrough'
            'dbms_iot'
            'dbms_job'
            'dbms_ldap'
            'dbms_libcache'
            'dbms_lob'
            'dbms_lock'
            'dbms_logmnr'
            'dbms_logmnr_cdc_publish'
            'dbms_logmnr_cdc_subscribe'
            'dbms_logmnr_d'
            'dbms_metadata'
            'dbms_mview'
            'dbms_obfuscation_toolkit'
            'dbms_odci'
            'dbms_offline_og'
            'dbms_offline_snapshot'
            'dbms_olap'
            'dbms_oracle_trace_agent'
            'dbms_oracle_trace_user'
            'dbms_outln'
            'dbms_outln_edit'
            'dbms_output'
            'dbms_pclxutil'
            'dbms_pipe'
            'dbms_profiler'
            'dbms_random'
            'dbms_rectifier_diff'
            'dbms_redefinition'
            'dbms_refresh'
            'dbms_repair'
            'dbms_repcat'
            'dbms_repcat_admin'
            'dbms_repcat_instatiate'
            'dbms_repcat_rgt'
            'dbms_reputil'
            'dbms_resource_manager'
            'dbms_resource_manager_privs'
            'dbms_resumable'
            'dbms_rls'
            'dbms_rowid'
            'dbms_session'
            'dbms_shared_pool'
            'dbms_snapshot'
            'dbms_space'
            'dbms_space_admin'
            'dbms_sql'
            'dbms_standard'
            'dbms_stats'
            'dbms_trace'
            'dbms_transaction'
            'dbms_transform'
            'dbms_tts'
            'dbms_types'
            'dbms_utility'
            'dbms_wm'
            'dbms_xmlgen'
            'dbms_xmlquery'
            'dbms_xmlsave'
            'debug_extproc'
            'deleting'
            'outln_pkg'
            'plitblm'
            'raise_application_error'
            'sdo_cs'
            'sdo_geom'
            'sdo_lrs'
            'sdo_migrate'
            'sdo_tune'
            'set_transaction_use'
            'standard'
            'utl_coll'
            'utl_encode'
            'utl_file'
            'utl_http'
            'utl_inaddr'
            'utl_pg'
            'utl_raw'
            'utl_ref'
            'utl_smtp'
            'utl_tcp'
            'utl_url')
          TokenTypes = 4
          IgnoreCase = True
        end>
      HighlightPos = cpAny
      IgnoreAsParent = False
    end
    item
      DisplayName = 'Predefined Exceptions'
      StyleName = 'Predefined Exceptions'
      BlockType = btTagDetect
      ConditionList = <
        item
          TagList.Strings = (
            'access_into_null'
            'collection_is_null'
            'cursor_already_open'
            'dbms_lob.access_error'
            'dbms_lob.invalid_directory'
            'dbms_lob.noexist_directory'
            'dbms_lob.nopriv_directory'
            'dbms_lob.open_toomany'
            'dbms_lob.operation_failed'
            'dbms_lob.unopened_file'
            'dbms_sql.inconsistent_type'
            'dup_val_on_index'
            'invalid_cursor'
            'invalid_number'
            'login_denied'
            'no_data_found'
            'not_logged_on'
            'others'
            'program_error'
            'storage_error'
            'subscript_beyond_count'
            'subscript_outside_limit'
            'timeout_on_resource'
            'too_many_rows'
            'utl_file.internal_error'
            'utl_file.invalid_filehandle'
            'utl_file.invalid_mode'
            'utl_file.invalid_operation'
            'utl_file.invalid_path'
            'utl_file.read_error'
            'utl_file.write_error'
            'value_error'
            'zero_divide')
          TokenTypes = 4
          IgnoreCase = True
        end>
      HighlightPos = cpAny
      IgnoreAsParent = False
    end
    item
      DisplayName = 'ToadFold'
      StyleName = 'Current block'
      ConditionList = <
        item
          TagList.Strings = (
            '/*startfold*/')
          TokenTypes = 2
          IgnoreCase = True
        end>
      BlockEnd = 'ToadFold <end>'
      DynHighlight = dhBound
      HighlightPos = cpRange
      DynSelectMin = True
      DrawStaple = True
      CollapseFmt = ' %s-1'
      IgnoreAsParent = False
    end
    item
      DisplayName = 'ToadFold <end>'
      BlockName = 'ToadFold'
      StrictParent = True
      BlockType = btRangeEnd
      ConditionList = <
        item
          TagList.Strings = (
            '/*endfold*/')
          TokenTypes = 2
          IgnoreCase = True
        end>
      HighlightPos = cpAny
      IgnoreAsParent = False
    end
    item
      DisplayName = 'Comment'
      BlockName = 'Comment'
      NotParent = True
      ConditionList = <
        item
          TokenTypes = 2
        end>
      BlockEnd = 'Comment <end>'
      DisplayInTree = False
      HighlightPos = cpAny
      CollapseFmt = 'Comments...'
      IgnoreAsParent = False
    end
    item
      DisplayName = 'Comment <end>'
      BlockType = btRangeEnd
      ConditionList = <
        item
          TokenTypes = 1021
        end
        item
          TokenTypes = 2
        end>
      BlockOffset = 1
      HighlightPos = cpAny
      IgnoreAsParent = False
    end
    item
      DisplayName = 'Constant'
      StrictParent = True
      ConditionList = <
        item
          TagList.Strings = (
            'constant')
          TokenTypes = 4
          IgnoreCase = True
        end
        item
          TokenTypes = 4
        end>
      BlockOffset = 1
      BlockEnd = 'Constant <end>'
      NotCollapsed = True
      NameFmt = '%s1 %s-1'
      GroupFmt = 'Constants'
      HighlightPos = cpAny
      TreeGroupStyle = 'Group (tree)'
      TreeItemImage = 4
      TreeGroupImage = 0
      IgnoreAsParent = False
    end
    item
      DisplayName = 'Constant <end>'
      BlockName = 'Constant'
      StrictParent = True
      BlockType = btRangeEnd
      ConditionList = <
        item
          TagList.Strings = (
            ';')
          TokenTypes = 8
        end>
      HighlightPos = cpAny
      IgnoreAsParent = False
    end
    item
      DisplayName = 'Case'
      StyleName = 'Current block'
      ConditionList = <
        item
          TagList.Strings = (
            'case')
          TokenTypes = 4
          IgnoreCase = True
        end
        item
          TagList.Strings = (
            'end')
          CondType = tcNotEqual
          TokenTypes = 4
          IgnoreCase = True
        end>
      BlockEnd = 'Case <end>'
      DisplayInTree = False
      DynHighlight = dhBound
      HighlightPos = cpRange
      DynSelectMin = True
      DrawStaple = True
      CollapseFmt = '%s0...'
      IgnoreAsParent = False
    end
    item
      DisplayName = 'Case <end>'
      BlockName = 'Case'
      StrictParent = True
      BlockType = btRangeEnd
      ConditionList = <
        item
          TagList.Strings = (
            'end')
          TokenTypes = 4
          IgnoreCase = True
        end>
      BlockEnd = 'Case'
      HighlightPos = cpAny
      IgnoreAsParent = False
    end
    item
      DisplayName = 'If'
      StyleName = 'Current block'
      ConditionList = <
        item
          TagList.Strings = (
            'if')
          TokenTypes = 4
          IgnoreCase = True
        end
        item
          TagList.Strings = (
            'end')
          CondType = tcNotEqual
          TokenTypes = 4
          IgnoreCase = True
        end>
      BlockEnd = 'If <end>'
      DynHighlight = dhBound
      HighlightPos = cpRange
      DynSelectMin = True
      DrawStaple = True
      CollapseFmt = '%s0...'
      IgnoreAsParent = False
    end
    item
      DisplayName = 'If <end>'
      BlockType = btRangeEnd
      ConditionList = <
        item
          TagList.Strings = (
            'if')
          TokenTypes = 4
          IgnoreCase = True
        end
        item
          TagList.Strings = (
            'end')
          TokenTypes = 4
          IgnoreCase = True
        end>
      HighlightPos = cpAny
      IgnoreAsParent = False
    end
    item
      DisplayName = 'DML'
      BlockName = 'DML'
      NotParent = True
      ConditionList = <
        item
          TagList.Strings = (
            'cursor'
            'delete'
            'insert'
            'select'
            'update    ')
          TokenTypes = 4
          IgnoreCase = True
        end>
      BlockEnd = 'DML <end>'
      RefToCondEnd = True
      HighlightPos = cpAny
      CollapseFmt = '%s0...'
      TreeItemImage = 6
      IgnoreAsParent = False
    end
    item
      DisplayName = 'DML <end>'
      BlockType = btRangeEnd
      ConditionList = <
        item
          TagList.Strings = (
            ';')
          TokenTypes = 8
        end>
      BlockEnd = 'DML'
      HighlightPos = cpAny
      IgnoreAsParent = False
    end
    item
      DisplayName = 'DDL (1)'
      ConditionList = <
        item
          TagList.Strings = (
            'cluster'
            'context'
            'dimension'
            'directory'
            'index'
            'library'
            'materialized'
            'profile'
            'public'
            'role'
            'rollback'
            'sequence'
            'table'
            'tablespace'
            'unique'
            'user'
            'view')
          TokenTypes = 4
          IgnoreCase = True
        end
        item
          TagList.Strings = (
            'create')
          TokenTypes = 4
          IgnoreCase = True
        end>
      BlockEnd = 'DDL (1) <end>'
      DisplayInTree = False
      RefToCondEnd = True
      HighlightPos = cpAny
      CollapseFmt = '%s0 %s-1...'
      IgnoreAsParent = False
    end
    item
      DisplayName = 'DDL (1) <end>'
      BlockName = 'DDL (1)'
      StrictParent = True
      BlockType = btRangeEnd
      ConditionList = <
        item
          TagList.Strings = (
            '/'
            ';')
          TokenTypes = 8
        end>
      BlockEnd = 'DDL (1)'
      HighlightPos = cpAny
      IgnoreAsParent = False
    end
    item
      DisplayName = 'DDL (2)'
      ConditionList = <
        item
          TagList.Strings = (
            'cluster'
            'context'
            'dimension'
            'directory'
            'index'
            'library'
            'materialized'
            'profile'
            'public'
            'role'
            'rollback'
            'sequence'
            'table'
            'tablespace'
            'unique'
            'user'
            'view')
          TokenTypes = 4
          IgnoreCase = True
        end
        item
          TagList.Strings = (
            'replace')
          TokenTypes = 4
          IgnoreCase = True
        end
        item
          TagList.Strings = (
            'or')
          TokenTypes = 4
          IgnoreCase = True
        end
        item
          TagList.Strings = (
            'create')
          TokenTypes = 4
          IgnoreCase = True
        end>
      BlockEnd = 'DDL (2) <end>'
      DisplayInTree = False
      HighlightPos = cpAny
      CollapseFmt = '%s0 %s-1 %s-2 %s-3...'
      IgnoreAsParent = False
    end
    item
      DisplayName = 'DDL (2) <end>'
      BlockName = 'DDL (2)'
      StrictParent = True
      BlockType = btRangeEnd
      ConditionList = <
        item
          TagList.Strings = (
            '/'
            ';')
          TokenTypes = 8
        end>
      BlockEnd = 'DDL (2)'
      HighlightPos = cpAny
      IgnoreAsParent = False
    end
    item
      DisplayName = 'Cursor Open'
      ConditionList = <
        item
          TagList.Strings = (
            'for')
          TokenTypes = 4
          IgnoreCase = True
        end
        item
          TokenTypes = 4
        end
        item
          TagList.Strings = (
            'open')
          TokenTypes = 4
          IgnoreCase = True
        end>
      BlockEnd = 'Cursor Open <end>'
      NameFmt = '%s0 %s-1'
      HighlightPos = cpAny
      CollapseFmt = '...'
      IgnoreAsParent = False
    end
    item
      DisplayName = 'Cursor Open <end>'
      BlockType = btRangeEnd
      ConditionList = <
        item
          TagList.Strings = (
            ';')
          TokenTypes = 8
        end>
      BlockEnd = 'Cursor Open'
      HighlightPos = cpAny
      IgnoreAsParent = False
    end
    item
      DisplayName = 'For'
      StyleName = 'Current block'
      ConditionList = <
        item
          TagList.Strings = (
            'loop')
          TokenTypes = 4
          IgnoreCase = True
        end
        item
          TokenTypes = 4
        end
        item
          TagList.Strings = (
            'for'
            'while')
          TokenTypes = 4
          IgnoreCase = True
        end>
      BlockEnd = 'For <end>'
      DisplayInTree = False
      RefToCondEnd = True
      DynHighlight = dhBound
      HighlightPos = cpRange
      DynSelectMin = True
      DrawStaple = True
      CollapseFmt = '%s0 %s-1 %s-2...'
      IgnoreAsParent = False
    end
    item
      DisplayName = 'For <end>'
      BlockName = 'For'
      StrictParent = True
      BlockType = btRangeEnd
      ConditionList = <
        item
          TagList.Strings = (
            'loop')
          TokenTypes = 4
          IgnoreCase = True
        end
        item
          TagList.Strings = (
            'end')
          TokenTypes = 4
          IgnoreCase = True
        end>
      HighlightPos = cpAny
      IgnoreAsParent = False
    end
    item
      DisplayName = 'Loop'
      StyleName = 'Current block'
      BlockName = 'For'
      NotParent = True
      ConditionList = <
        item
          TagList.Strings = (
            'loop')
          TokenTypes = 4
          IgnoreCase = True
        end
        item
          TagList.Strings = (
            'end')
          CondType = tcNotEqual
          TokenTypes = 4
          IgnoreCase = True
        end>
      BlockEnd = 'Loop <end>'
      DynHighlight = dhBound
      HighlightPos = cpRange
      DynSelectMin = True
      DrawStaple = True
      CollapseFmt = '%s0...'
      IgnoreAsParent = False
    end
    item
      DisplayName = 'Loop <end>'
      BlockType = btRangeEnd
      ConditionList = <
        item
          TagList.Strings = (
            'loop')
          TokenTypes = 4
          IgnoreCase = True
        end
        item
          TagList.Strings = (
            'end')
          TokenTypes = 4
          IgnoreCase = True
        end>
      HighlightPos = cpAny
      IgnoreAsParent = False
    end
    item
      DisplayName = 'Function Separator'
      StyleName = 'Function separator'
      BlockType = btLineBreak
      ConditionList = <
        item
          TagList.Strings = (
            'function'
            'procedure')
          TokenTypes = 4
          IgnoreCase = True
        end>
      HighlightPos = cpAny
      IgnoreAsParent = False
    end
    item
      DisplayName = '('
      StyleName = '()'
      ConditionList = <
        item
          TagList.Strings = (
            '(')
          TokenTypes = 8
        end>
      BlockEnd = ')'
      DisplayInTree = False
      DynHighlight = dhBound
      HighlightPos = cpRange
      DynSelectMin = True
      CollapseFmt = '( ... )'
      IgnoreAsParent = False
    end
    item
      DisplayName = ')'
      BlockType = btRangeEnd
      ConditionList = <
        item
          TagList.Strings = (
            ')')
          TokenTypes = 8
        end>
      BlockEnd = '('
      HighlightPos = cpAny
      IgnoreAsParent = False
    end
    item
      DisplayName = 'Exception'
      ConditionList = <
        item
          TagList.Strings = (
            'exception')
          TokenTypes = 4
          IgnoreCase = True
        end>
      BlockEnd = 'Exception <end>'
      DisplayInTree = False
      NameFmt = '%s0 %s-2'
      HighlightPos = cpAny
      DrawStaple = True
      CollapseFmt = '%s0 ...'
      IgnoreAsParent = False
    end
    item
      DisplayName = 'Exception <end>'
      BlockName = 'Exception'
      StrictParent = True
      BlockType = btRangeEnd
      ConditionList = <
        item
          TagList.Strings = (
            'end')
          TokenTypes = 4
          IgnoreCase = True
        end>
      HighlightPos = cpAny
      IgnoreAsParent = False
    end
    item
      DisplayName = 'Alter'
      ConditionList = <
        item
          TagList.Strings = (
            'alter')
          TokenTypes = 4
          IgnoreCase = True
        end>
      BlockEnd = 'Alter <end>'
      HighlightPos = cpAny
      CollapseFmt = '%s0 %s-1...'
      IgnoreAsParent = False
    end
    item
      DisplayName = 'Alter <end>'
      BlockName = 'Alter'
      StrictParent = True
      BlockType = btRangeEnd
      ConditionList = <
        item
          TagList.Strings = (
            ';')
          TokenTypes = 8
        end>
      BlockEnd = 'Alter'
      HighlightPos = cpAny
      IgnoreAsParent = False
    end
    item
      DisplayName = 'Begin'
      StyleName = 'Current block'
      ConditionList = <
        item
          TagList.Strings = (
            'begin')
          TokenTypes = 4
          IgnoreCase = True
        end>
      BlockEnd = 'Begin <end>'
      DynHighlight = dhBound
      HighlightPos = cpRange
      DynSelectMin = True
      DrawStaple = True
      CollapseFmt = '%s0...'
      IgnoreAsParent = False
    end
    item
      DisplayName = 'Begin <end>'
      BlockName = 'Begin'
      StrictParent = True
      BlockType = btRangeEnd
      ConditionList = <
        item
          TagList.Strings = (
            'case'
            'if'
            'loop')
          CondType = tcNotEqual
          TokenTypes = 4
          IgnoreCase = True
        end
        item
          TagList.Strings = (
            'end')
          TokenTypes = 4
          IgnoreCase = True
        end>
      HighlightPos = cpAny
      IgnoreAsParent = False
    end
    item
      DisplayName = 'PLSQL (1)'
      ConditionList = <
        item
          TagList.Strings = (
            'function'
            'package'
            'procedure'
            'trigger'
            'type')
          TokenTypes = 4
          IgnoreCase = True
        end
        item
          TagList.Strings = (
            'create')
          TokenTypes = 4
          IgnoreCase = True
        end>
      BlockEnd = 'PLSQL <end>'
      DisplayInTree = False
      RefToCondEnd = True
      HighlightPos = cpAny
      CollapseFmt = '%s0 %s-1 %s-2...'
      IgnoreAsParent = False
    end
    item
      DisplayName = 'PLSQL (2)'
      ConditionList = <
        item
          TagList.Strings = (
            'function'
            'java'
            'package'
            'procedure'
            'trigger'
            'type')
          TokenTypes = 4
          IgnoreCase = True
        end
        item
          TagList.Strings = (
            'replace')
          TokenTypes = 4
          IgnoreCase = True
        end
        item
          TagList.Strings = (
            'or')
          TokenTypes = 4
          IgnoreCase = True
        end
        item
          TagList.Strings = (
            'create')
          TokenTypes = 4
          IgnoreCase = True
        end>
      BlockEnd = 'PLSQL <end>'
      DisplayInTree = False
      RefToCondEnd = True
      HighlightPos = cpAny
      CollapseFmt = '%s0 %s-1 %s-2 %s-3 %s-4...'
      IgnoreAsParent = False
    end
    item
      DisplayName = 'PLSQL <end>'
      BlockType = btRangeEnd
      ConditionList = <
        item
          TagList.Strings = (
            '/')
          TokenTypes = 8
        end
        item
          TagList.Strings = (
            ';')
          TokenTypes = 8
          IgnoreCase = True
        end
        item
          CondType = tcSkip
          TokenTypes = 4
          IgnoreCase = True
        end
        item
          TagList.Strings = (
            'end')
          TokenTypes = 4
          IgnoreCase = True
        end>
      HighlightPos = cpAny
      IgnoreAsParent = False
    end
    item
      DisplayName = 'Java Source (1)'
      ConditionList = <
        item
          TagList.Strings = (
            'named')
          TokenTypes = 4
          IgnoreCase = True
        end
        item
          TagList.Strings = (
            'source')
          TokenTypes = 4
          IgnoreCase = True
        end
        item
          TagList.Strings = (
            'java')
          TokenTypes = 4
          IgnoreCase = True
        end
        item
          TagList.Strings = (
            'create')
          TokenTypes = 4
          IgnoreCase = True
        end>
      BlockEnd = 'Java Source <end>'
      DisplayInTree = False
      RefToCondEnd = True
      HighlightPos = cpAny
      CollapseFmt = '%s0 %s-1 %s-2...'
      IgnoreAsParent = False
    end
    item
      DisplayName = 'Java Source (2)'
      ConditionList = <
        item
          TagList.Strings = (
            'named')
          TokenTypes = 4
          IgnoreCase = True
        end
        item
          TagList.Strings = (
            'source')
          TokenTypes = 4
          IgnoreCase = True
        end
        item
          TagList.Strings = (
            'java')
          TokenTypes = 4
          IgnoreCase = True
        end
        item
          TagList.Strings = (
            'replace')
          TokenTypes = 4
          IgnoreCase = True
        end
        item
          TagList.Strings = (
            'or')
          TokenTypes = 4
          IgnoreCase = True
        end
        item
          TagList.Strings = (
            'create')
          TokenTypes = 4
          IgnoreCase = True
        end>
      BlockEnd = 'Java Source <end>'
      DisplayInTree = False
      RefToCondEnd = True
      HighlightPos = cpAny
      CollapseFmt = '%s0 %s-1 %s-2 %s-3 %s-4...'
      IgnoreAsParent = False
    end
    item
      DisplayName = 'Java Source <end>'
      BlockType = btRangeEnd
      ConditionList = <
        item
          TagList.Strings = (
            '/')
          TokenTypes = 8
          IgnoreCase = True
        end
        item
          TagList.Strings = (
            '}')
          TokenTypes = 8
          IgnoreCase = True
        end>
      HighlightPos = cpAny
      IgnoreAsParent = False
    end
    item
      DisplayName = 'Package Procedure'
      ConditionList = <
        item
          TagList.Strings = (
            'function'
            'procedure')
          TokenTypes = 4
          IgnoreCase = True
        end
        item
          TagList.Strings = (
            'create'
            'replace')
          CondType = tcNotEqual
          TokenTypes = 4
          IgnoreCase = True
        end>
      DisplayInTree = False
      RefToCondEnd = True
      HighlightPos = cpAny
      CollapseFmt = '%s0 %s-1...'
      IgnoreAsParent = False
    end
    item
      DisplayName = 'Package Procedure (1) <end>'
      BlockName = 'Package Procedure'
      StrictParent = True
      BlockType = btRangeEnd
      ConditionList = <
        item
          TagList.Strings = (
            ';')
          TokenTypes = 8
        end
        item
          TagList.Strings = (
            'end')
          TokenTypes = 4
          IgnoreCase = True
        end>
      BlockEnd = 'Package Procedure'
      HighlightPos = cpAny
      IgnoreAsParent = False
    end
    item
      DisplayName = 'Package Procedure (2) <end>'
      BlockName = 'Package Procedure'
      StrictParent = True
      BlockType = btRangeEnd
      ConditionList = <
        item
          TagList.Strings = (
            ';')
          TokenTypes = 8
          IgnoreCase = True
        end
        item
          TagList.Strings = (
            'if'
            'loop')
          CondType = tcNotEqual
          TokenTypes = 4
        end
        item
          TagList.Strings = (
            'end')
          TokenTypes = 4
          IgnoreCase = True
        end>
      BlockEnd = 'Package Procedure'
      HighlightPos = cpAny
      IgnoreAsParent = False
    end
    item
      DisplayName = 'Toad_Datatypes'
      StyleName = 'Toad_Datatypes'
      BlockType = btTagDetect
      ConditionList = <>
      HighlightPos = cpAny
      IgnoreAsParent = False
    end
    item
      DisplayName = 'Toad_UserTables'
      StyleName = 'Toad_UserTables'
      BlockType = btTagDetect
      ConditionList = <>
      HighlightPos = cpAny
      IgnoreAsParent = False
    end
    item
      DisplayName = 'Toad_UserViews'
      StyleName = 'Toad_UserViews'
      BlockType = btTagDetect
      ConditionList = <>
      HighlightPos = cpAny
      IgnoreAsParent = False
    end
    item
      DisplayName = 'Toad_UserProcs'
      StyleName = 'Toad_UserProcs'
      BlockType = btTagDetect
      ConditionList = <>
      HighlightPos = cpAny
      IgnoreAsParent = False
    end
    item
      DisplayName = 'Toad_SYSViews'
      StyleName = 'Toad_SYSViews'
      BlockType = btTagDetect
      ConditionList = <>
      HighlightPos = cpAny
      IgnoreAsParent = False
    end>
  CodeTemplates = <>
  SubAnalyzers = <
    item
      DisplayName = 'Java'
      StartExpression = 
        '(?i)(\bJAVA\b)(\s*?)(\bSOURCE\b)(\s*?)(\bNAMED\b)(s*?)(.+?)(\b(A' +
        'S|IS)\b)'
      EndExpression = '(?-m)(\})(\s*?)(;?\s*?)((\/\s*?(\r|\n|$))|(\s*?$))'
    end>
  SampleText.Strings = (
    '/* This calculates a nextdate for a defined interval.'
    
      '   The function works similar to the functionality in DBMS_JOB *' +
      '/'
    ''
    'FUNCTION calc_next_date ('
    '   i_interval   IN   VARCHAR2,'
    '   i_default    IN   DATE DEFAULT SYSDATE'
    ')'
    '   RETURN DATE'
    'IS'
    '   v_date        DATE;'
    '   v_statement   VARCHAR2 (5000);'
    'BEGIN'
    '   IF i_interval IS NULL'
    '   THEN'
    
      '      RETURN (i_default);  -- if no interval defined return the ' +
      'default value'
    '   ELSE'
    '      v_statement := '#39'SELECT '#39' || i_interval || '#39' FROM DUAL'#39';'
    ''
    '      EXECUTE IMMEDIATE v_statement'
    '                   INTO v_date;'
    ''
    '      RETURN (v_date);'
    '   END IF;'
    'EXCEPTION'
    '   WHEN OTHERS'
    '   THEN'
    '      -- if any error occurs, return the default value'
    '      RETURN (i_default);'
    'END calc_next_date;')
  TokenTypeNames.Strings = (
    'Unknown'
    'Comment'
    'Identifier'
    'Symbol'
    'String'
    'Integer const'
    'Float const')
  Gramma.Gramma = 
    'Skip = <Comment>;'#13#10#13#10'Table = TblBreak <Identifier> OptionAlias;'#13 +
    #10#13#10'Table2 = TblBreak <Identifier> ('#39'.'#39' | '#39'@'#39') '#13#10'          <Ident' +
    'ifier> OptionAlias;'#13#10#13#10'Table3 = TblBreak <Identifier> '#39'.'#39' <Ident' +
    'ifier> '#39'@'#39' <Identifier> OptionAlias;'#13#10#13#10'OptionAlias = <Identifie' +
    'r> | TblBreak;'#13#10#13#10'Alias = <Identifier> <Identifier> TblBreak; '#13#10 +
    #13#10'TblBreak = <Section> | '#39','#39';'
  MarkedBlockStyle = 'Marked block'
  CurrentLineStyle = 'Current Line'
  DefaultStyleName = 'Default'
  Extentions = 'SQL FNC JAVA JVS PKB PKS PRC TPB TPS TRG VW PLS'
  LexerName = 'PL/SQL'
  Internal = True
  RestartFromLineStart = True
  LineComment = '--'
  Left = 144
  Top = 184
end
