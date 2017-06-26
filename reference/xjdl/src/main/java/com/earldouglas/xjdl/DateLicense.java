/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.earldouglas.xjdl;

import java.util.Date;

import com.earldouglas.xjdl.exception.LicenseExpiredException;
import com.earldouglas.xjdl.exception.LicenseInvalidException;
import com.earldouglas.xjdl.exception.LicenseNotYetActiveException;

@SuppressWarnings("serial")
public class DateLicense implements License {

	private Date begin;
	private Date end;

	public void setBegin(Date begin) {
		this.begin = begin;
	}

	public Date getBegin() {
		return begin;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public Date getEnd() {
		return end;
	}

	@Override
	public void authorize() {
		if ((begin == null && end == null) || (begin != null && end != null && begin.after(end))) {
			throw new LicenseInvalidException();
		} else if (begin != null && begin.after(new Date())) {
			throw new LicenseNotYetActiveException();
		} else if (end != null && end.before(new Date())) {
			throw new LicenseExpiredException();
		}
	}
}
