/*
 * CSS Parser Project
 *
 * Copyright (C) 1999-2011 David Schweinsberg.  All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * To contact the authors of the library:
 *
 * http://cssparser.sourceforge.net/
 * mailto:davidsch@users.sourceforge.net
 *
 */

package com.steadystate.css.parser;

import java.util.ListResourceBundle;

/**
 * ExceptionResource.
 *
 * @author <a href="mailto:davidsch@users.sourceforge.net">David Schweinsberg</a>
 * @author rbri
 */
public class ExceptionResource extends ListResourceBundle {

    @Override
    public Object[][] getContents() {
        return contents;
    }

    static final Object[][] contents = {
        {"s0", "Syntax error"},
        {"s1", "Index out of bounds error"},
        {"s2", "This style sheet is read only"},
        {"s3", "The text does not represent an unknown rule"},
        {"s4", "The text does not represent a style rule"},
        {"s5", "The text does not represent a charset rule"},
        {"s6", "The text does not represent an import rule"},
        {"s7", "The text does not represent a media rule"},
        {"s8", "The text does not represent a font face rule"},
        {"s9", "The text does not represent a page rule"},
        {"s10", "This isn't a Float type"},
        {"s11", "This isn't a String type"},
        {"s12", "This isn't a Counter type"},
        {"s13", "This isn't a Rect type"},
        {"s14", "This isn't an RGBColor type"},
        {"s15", "A charset rule must be the first rule"},
        {"s16", "A charset rule already exists"},
        {"s17", "An import rule must preceed all other rules"},
        {"s18", "The specified type was not found"},
        {"s20", "Can't insert a rule before the last charset or import rule"}
    };
}
