package com.smart.core.centralized.wallet.profilings.config;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PrefixPhysicalNamingStrategy extends PhysicalNamingStrategyStandardImpl {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * TODO Make this an injectable application property
	 */
	public static final String TABLE_NAME_PREFIX = "WALLET_";

	@Override
	public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
		Identifier newIdentifier = new Identifier(TABLE_NAME_PREFIX + name.getText(), name.isQuoted());
		return super.toPhysicalTableName(newIdentifier, context);
	}
}
