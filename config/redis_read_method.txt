    public static ${NAME1} read${NAME1}(byte[] key)
    {
    	byte[] data = PlayerCacheMgr.get(key);

    	if (null == data)
            return null;

    	try
    	{
        	${NAME2} proto = ${NAME2}.parseFrom(data);
        	${NAME1} info = new ${NAME1}();
${TEMP1}
      
        	return info;
    	}
    	catch (Exception e)
    	{
            LOGGER.error("{}", new String(key), e);
    	}

    	return null;
    }