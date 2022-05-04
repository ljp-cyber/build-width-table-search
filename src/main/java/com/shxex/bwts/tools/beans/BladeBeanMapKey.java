package com.shxex.bwts.tools.beans;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

/**
 * bean map key，提高性能
 *
 * @author L.cm
 */
@EqualsAndHashCode
@AllArgsConstructor
public class BladeBeanMapKey {
	private final Class type;
	private final int require;
}
