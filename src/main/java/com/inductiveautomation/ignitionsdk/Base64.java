package com.inductiveautomation.ignitionsdk;

import java.io.IOException;


public class Base64 {

	/* P U B L I C F I E L D S */

    /** No options specified. Value is zero. */
    public final static int NO_OPTIONS = 0;

    /** Specify encoding. */
    public final static int ENCODE = 1;

    /** Specify decoding. */
    public final static int DECODE = 0;

    /** Specify that data should be gzip-compressed. */
    public final static int GZIP = 2;

    /** Don't break lines when encoding (violates strict Base64 specification) */
    public final static int DONT_BREAK_LINES = 8;

    /**
     * Encode using Base64-like encoding that is URL- and Filename-safe as described in Section 4 of RFC3548: <a
     * href="http://www.faqs.org/rfcs/rfc3548.html">http://www.faqs.org/rfcs/rfc3548.html</a>. It is important to note
     * that data encoded this way is <em>not</em> officially valid Base64, or at the very least should not be called
     * Base64 without also specifying that is was encoded using the URL- and Filename-safe dialect.
     */
    public final static int URL_SAFE = 16;

    /**
     * Encode using the special "ordered" dialect of Base64 described here: <a
     * href="http://www.faqs.org/qa/rfcc-1940.html">http://www.faqs.org/qa/rfcc-1940.html</a>.
     */
    public final static int ORDERED = 32;

	/* P R I V A T E F I E L D S */

    /** Maximum line length (76) of Base64 output. */
    private final static int MAX_LINE_LENGTH = 76;

    /** The equals sign (=) as a byte. */
    private final static byte EQUALS_SIGN = (byte) '=';

    /** The new line character (\n) as a byte. */
    private final static byte NEW_LINE = (byte) '\n';

    /** Preferred encoding. */
    private final static String PREFERRED_ENCODING = "ISO-8859-1";

    // I think I end up not using the BAD_ENCODING indicator.
    //private final static byte BAD_ENCODING    = -9; // Indicates error in encoding
    private final static byte WHITE_SPACE_ENC = -5; // Indicates white space in encoding
    private final static byte EQUALS_SIGN_ENC = -1; // Indicates equals sign in encoding

	/* S T A N D A R D B A S E 6 4 A L P H A B E T */

    /** The 64 valid Base64 values. */
    //private final static byte[] ALPHABET;
	/* Host platform me be something funny like EBCDIC, so we hardcode these values. */
    private final static byte[] _STANDARD_ALPHABET = { (byte) 'A', (byte) 'B', (byte) 'C', (byte) 'D', (byte) 'E',
            (byte) 'F', (byte) 'G', (byte) 'H', (byte) 'I', (byte) 'J', (byte) 'K', (byte) 'L', (byte) 'M', (byte) 'N',
            (byte) 'O', (byte) 'P', (byte) 'Q', (byte) 'R', (byte) 'S', (byte) 'T', (byte) 'U', (byte) 'V', (byte) 'W',
            (byte) 'X', (byte) 'Y', (byte) 'Z', (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f',
            (byte) 'g', (byte) 'h', (byte) 'i', (byte) 'j', (byte) 'k', (byte) 'l', (byte) 'm', (byte) 'n', (byte) 'o',
            (byte) 'p', (byte) 'q', (byte) 'r', (byte) 's', (byte) 't', (byte) 'u', (byte) 'v', (byte) 'w', (byte) 'x',
            (byte) 'y', (byte) 'z', (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4', (byte) '5', (byte) '6',
            (byte) '7', (byte) '8', (byte) '9', (byte) '+', (byte) '/' };

    /**
     * Translates a Base64 value to either its 6-bit reconstruction value or a negative number indicating some other
     * meaning.
     **/
    private final static byte[] _STANDARD_DECODABET = { -9, -9, -9, -9, -9, -9, -9, -9, -9, // Decimal  0 -  8
            -5, -5, // Whitespace: Tab and Linefeed
            -9, -9, // Decimal 11 - 12
            -5, // Whitespace: Carriage Return
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, // Decimal 14 - 26
            -9, -9, -9, -9, -9, // Decimal 27 - 31
            -5, // Whitespace: Space
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, // Decimal 33 - 42
            62, // Plus sign at decimal 43
            -9, -9, -9, // Decimal 44 - 46
            63, // Slash at decimal 47
            52, 53, 54, 55, 56, 57, 58, 59, 60, 61, // Numbers zero through nine
            -9, -9, -9, // Decimal 58 - 60
            -1, // Equals sign at decimal 61
            -9, -9, -9, // Decimal 62 - 64
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, // Letters 'A' through 'N'
            14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, // Letters 'O' through 'Z'
            -9, -9, -9, -9, -9, -9, // Decimal 91 - 96
            26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, // Letters 'a' through 'm'
            39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, // Letters 'n' through 'z'
            -9, -9, -9, -9 // Decimal 123 - 126
			/*
			 * ,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 127 - 139 -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, //
			 * Decimal
			 * 140 - 152 -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 153 - 165
			 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, //
			 * Decimal 166 - 178 -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 179 - 191
			 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 192 - 204 -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, //
			 * Decimal
			 * 205 - 217 -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 218 - 230
			 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, //
			 * Decimal 231 - 243 -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9 // Decimal 244 - 255
			 */
    };

	/* U R L S A F E B A S E 6 4 A L P H A B E T */

    /**
     * Used in the URL- and Filename-safe dialect described in Section 4 of RFC3548: <a
     * href="http://www.faqs.org/rfcs/rfc3548.html">http://www.faqs.org/rfcs/rfc3548.html</a>. Notice that the last two
     * bytes become "hyphen" and "underscore" instead of "plus" and "slash."
     */
    private final static byte[] _URL_SAFE_ALPHABET = { (byte) 'A', (byte) 'B', (byte) 'C', (byte) 'D', (byte) 'E',
            (byte) 'F', (byte) 'G', (byte) 'H', (byte) 'I', (byte) 'J', (byte) 'K', (byte) 'L', (byte) 'M', (byte) 'N',
            (byte) 'O', (byte) 'P', (byte) 'Q', (byte) 'R', (byte) 'S', (byte) 'T', (byte) 'U', (byte) 'V', (byte) 'W',
            (byte) 'X', (byte) 'Y', (byte) 'Z', (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f',
            (byte) 'g', (byte) 'h', (byte) 'i', (byte) 'j', (byte) 'k', (byte) 'l', (byte) 'm', (byte) 'n', (byte) 'o',
            (byte) 'p', (byte) 'q', (byte) 'r', (byte) 's', (byte) 't', (byte) 'u', (byte) 'v', (byte) 'w', (byte) 'x',
            (byte) 'y', (byte) 'z', (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4', (byte) '5', (byte) '6',
            (byte) '7', (byte) '8', (byte) '9', (byte) '-', (byte) '_' };

    /**
     * Used in decoding URL- and Filename-safe dialects of Base64.
     */
    private final static byte[] _URL_SAFE_DECODABET = { -9, -9, -9, -9, -9, -9, -9, -9, -9, // Decimal  0 -  8
            -5, -5, // Whitespace: Tab and Linefeed
            -9, -9, // Decimal 11 - 12
            -5, // Whitespace: Carriage Return
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, // Decimal 14 - 26
            -9, -9, -9, -9, -9, // Decimal 27 - 31
            -5, // Whitespace: Space
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, // Decimal 33 - 42
            -9, // Plus sign at decimal 43
            -9, // Decimal 44
            62, // Minus sign at decimal 45
            -9, // Decimal 46
            -9, // Slash at decimal 47
            52, 53, 54, 55, 56, 57, 58, 59, 60, 61, // Numbers zero through nine
            -9, -9, -9, // Decimal 58 - 60
            -1, // Equals sign at decimal 61
            -9, -9, -9, // Decimal 62 - 64
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, // Letters 'A' through 'N'
            14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, // Letters 'O' through 'Z'
            -9, -9, -9, -9, // Decimal 91 - 94
            63, // Underscore at decimal 95
            -9, // Decimal 96
            26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, // Letters 'a' through 'm'
            39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, // Letters 'n' through 'z'
            -9, -9, -9, -9 // Decimal 123 - 126
			/*
			 * ,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 127 - 139 -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, //
			 * Decimal
			 * 140 - 152 -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 153 - 165
			 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, //
			 * Decimal 166 - 178 -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 179 - 191
			 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 192 - 204 -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, //
			 * Decimal
			 * 205 - 217 -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 218 - 230
			 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, //
			 * Decimal 231 - 243 -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9 // Decimal 244 - 255
			 */
    };

	/* O R D E R E D B A S E 6 4 A L P H A B E T */

    /**
     * I don't get the point of this technique, but it is described here: <a
     * href="http://www.faqs.org/qa/rfcc-1940.html">http://www.faqs.org/qa/rfcc-1940.html</a>.
     */
    private final static byte[] _ORDERED_ALPHABET = { (byte) '-', (byte) '0', (byte) '1', (byte) '2', (byte) '3',
            (byte) '4', (byte) '5', (byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) 'A', (byte) 'B', (byte) 'C',
            (byte) 'D', (byte) 'E', (byte) 'F', (byte) 'G', (byte) 'H', (byte) 'I', (byte) 'J', (byte) 'K', (byte) 'L',
            (byte) 'M', (byte) 'N', (byte) 'O', (byte) 'P', (byte) 'Q', (byte) 'R', (byte) 'S', (byte) 'T', (byte) 'U',
            (byte) 'V', (byte) 'W', (byte) 'X', (byte) 'Y', (byte) 'Z', (byte) '_', (byte) 'a', (byte) 'b', (byte) 'c',
            (byte) 'd', (byte) 'e', (byte) 'f', (byte) 'g', (byte) 'h', (byte) 'i', (byte) 'j', (byte) 'k', (byte) 'l',
            (byte) 'm', (byte) 'n', (byte) 'o', (byte) 'p', (byte) 'q', (byte) 'r', (byte) 's', (byte) 't', (byte) 'u',
            (byte) 'v', (byte) 'w', (byte) 'x', (byte) 'y', (byte) 'z' };

    /**
     * Used in decoding the "ordered" dialect of Base64.
     */
    private final static byte[] _ORDERED_DECODABET = { -9, -9, -9, -9, -9, -9, -9, -9, -9, // Decimal  0 -  8
            -5, -5, // Whitespace: Tab and Linefeed
            -9, -9, // Decimal 11 - 12
            -5, // Whitespace: Carriage Return
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, // Decimal 14 - 26
            -9, -9, -9, -9, -9, // Decimal 27 - 31
            -5, // Whitespace: Space
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, // Decimal 33 - 42
            -9, // Plus sign at decimal 43
            -9, // Decimal 44
            0, // Minus sign at decimal 45
            -9, // Decimal 46
            -9, // Slash at decimal 47
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10, // Numbers zero through nine
            -9, -9, -9, // Decimal 58 - 60
            -1, // Equals sign at decimal 61
            -9, -9, -9, // Decimal 62 - 64
            11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, // Letters 'A' through 'M'
            24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, // Letters 'N' through 'Z'
            -9, -9, -9, -9, // Decimal 91 - 94
            37, // Underscore at decimal 95
            -9, // Decimal 96
            38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, // Letters 'a' through 'm'
            51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, // Letters 'n' through 'z'
            -9, -9, -9, -9 // Decimal 123 - 126
			/*
			 * ,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 127 - 139 -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, //
			 * Decimal
			 * 140 - 152 -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 153 - 165
			 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, //
			 * Decimal 166 - 178 -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 179 - 191
			 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 192 - 204 -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, //
			 * Decimal
			 * 205 - 217 -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 218 - 230
			 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, //
			 * Decimal 231 - 243 -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9 // Decimal 244 - 255
			 */
    };

	/* D E T E R M I N E W H I C H A L H A B E T */

    /**
     * Returns one of the _SOMETHING_ALPHABET byte arrays depending on the options specified. It's possible, though
     * silly, to specify ORDERED and URLSAFE in which case one of them will be picked, though there is no guarantee as
     * to
     * which one will be picked.
     */
    private final static byte[] getAlphabet(int options) {
        if ((options & URL_SAFE) == URL_SAFE)
            return _URL_SAFE_ALPHABET;
        else if ((options & ORDERED) == ORDERED)
            return _ORDERED_ALPHABET;
        else
            return _STANDARD_ALPHABET;

    } // end getAlphabet

    /**
     * Returns one of the _SOMETHING_DECODABET byte arrays depending on the options specified. It's possible, though
     * silly, to specify ORDERED and URL_SAFE in which case one of them will be picked, though there is no guarantee as
     * to which one will be picked.
     */
    private final static byte[] getDecodabet(int options) {
        if ((options & URL_SAFE) == URL_SAFE)
            return _URL_SAFE_DECODABET;
        else if ((options & ORDERED) == ORDERED)
            return _ORDERED_DECODABET;
        else
            return _STANDARD_DECODABET;

    } // end getAlphabet

    /** Defeats instantiation. */
    private Base64() {
    }

    /**
     * Encodes or decodes two files from the command line; <strong>feel free to delete this method (in fact you probably
     * should) if you're embedding this code into a larger program.</strong>
     */
    public final static void main(String[] args) {
        if (args.length < 3) {
            usage("Not enough arguments.");
        } // end if: args.length < 3
        else {
            String flag = args[0];
            String infile = args[1];
            String outfile = args[2];
            if (flag.equals("-e")) {
                Base64.encodeFileToFile(infile, outfile);
            } // end if: encode
            else if (flag.equals("-d")) {
                Base64.decodeFileToFile(infile, outfile);
            } // end else if: decode
            else {
                usage("Unknown flag: " + flag);
            } // end else
        } // end else
    } // end main


    private final static void usage(String msg) {
        System.err.println(msg);
        System.err.println("Usage: java Base64 -e|-d inputfile outputfile");
    } // end usage

	/* E N C O D I N G M E T H O D S */


    private static byte[] encode3to4(byte[] b4, byte[] threeBytes, int numSigBytes, int options) {
        encode3to4(threeBytes, 0, numSigBytes, b4, 0, options);
        return b4;
    } // end encode3to4


    private static byte[] encode3to4(byte[] source, int srcOffset, int numSigBytes, byte[] destination, int destOffset,
                                     int options) {
        byte[] ALPHABET = getAlphabet(options);

        //           1         2         3
        // 01234567890123456789012345678901 Bit position
        // --------000000001111111122222222 Array position from threeBytes
        // --------|    ||    ||    ||    | Six bit groups to index ALPHABET
        //          >>18  >>12  >> 6  >> 0  Right shift necessary
        //                0x3f  0x3f  0x3f  Additional AND

        // Create buffer with zero-padding if there are only one or two
        // significant bytes passed in the array.
        // We have to shift left 24 in order to flush out the 1's that appear
        // when Java treats a value as negative that is cast from a byte to an int.
        int inBuff = (numSigBytes > 0 ? ((source[srcOffset] << 24) >>> 8) : 0)
                | (numSigBytes > 1 ? ((source[srcOffset + 1] << 24) >>> 16) : 0)
                | (numSigBytes > 2 ? ((source[srcOffset + 2] << 24) >>> 24) : 0);

        switch (numSigBytes) {
            case 3:
                destination[destOffset] = ALPHABET[(inBuff >>> 18)];
                destination[destOffset + 1] = ALPHABET[(inBuff >>> 12) & 0x3f];
                destination[destOffset + 2] = ALPHABET[(inBuff >>> 6) & 0x3f];
                destination[destOffset + 3] = ALPHABET[(inBuff) & 0x3f];
                return destination;

            case 2:
                destination[destOffset] = ALPHABET[(inBuff >>> 18)];
                destination[destOffset + 1] = ALPHABET[(inBuff >>> 12) & 0x3f];
                destination[destOffset + 2] = ALPHABET[(inBuff >>> 6) & 0x3f];
                destination[destOffset + 3] = EQUALS_SIGN;
                return destination;

            case 1:
                destination[destOffset] = ALPHABET[(inBuff >>> 18)];
                destination[destOffset + 1] = ALPHABET[(inBuff >>> 12) & 0x3f];
                destination[destOffset + 2] = EQUALS_SIGN;
                destination[destOffset + 3] = EQUALS_SIGN;
                return destination;

            default:
                return destination;
        } // end switch
    } // end encode3to4


    public static String encodeObject(java.io.Serializable serializableObject) throws IOException {
        return encodeObject(serializableObject, NO_OPTIONS);
    } // end encodeObject


    public static String encodeObject(java.io.Serializable serializableObject, int options) throws IOException {
        // Streams
        java.io.ByteArrayOutputStream baos = null;
        java.io.OutputStream b64os = null;
        java.io.ObjectOutputStream oos = null;
        java.util.zip.GZIPOutputStream gzos = null;

        // Isolate options
        int gzip = (options & GZIP);
        int dontBreakLines = (options & DONT_BREAK_LINES);

        try {
            // ObjectOutputStream -> (GZIP) -> Base64 -> ByteArrayOutputStream
            baos = new java.io.ByteArrayOutputStream();
            b64os = new Base64.OutputStream(baos, ENCODE | options);

            // GZip?
            if (gzip == GZIP) {
                gzos = new java.util.zip.GZIPOutputStream(b64os);
                oos = new java.io.ObjectOutputStream(gzos);
            } // end if: gzip
            else
                oos = new java.io.ObjectOutputStream(b64os);

            oos.writeObject(serializableObject);
        } finally {
            try {
                oos.close();
            } catch (Exception e) {
            }
            try {
                gzos.close();
            } catch (Exception e) {
            }
            try {
                b64os.close();
            } catch (Exception e) {
            }
            try {
                baos.close();
            } catch (Exception e) {
            }
        } // end finally

        // Return value according to relevant encoding.
        try {
            return new String(baos.toByteArray(), PREFERRED_ENCODING);
        } // end try
        catch (java.io.UnsupportedEncodingException uue) {
            return new String(baos.toByteArray());
        } // end catch

    } // end encode


    public static String encodeBytes(byte[] source) {
        return encodeBytes(source, 0, source.length, NO_OPTIONS);
    } // end encodeBytes

    public static String encodeBytes(byte[] source, int options) {
        return encodeBytes(source, 0, source.length, options);
    } // end encodeBytes


    public static String encodeBytes(byte[] source, int off, int len) {
        return encodeBytes(source, off, len, NO_OPTIONS);
    } // end encodeBytes


    public static String encodeBytes(byte[] source, int off, int len, int options) {
        // Isolate options
        int dontBreakLines = (options & DONT_BREAK_LINES);
        int gzip = (options & GZIP);

        // Compress?
        if (gzip == GZIP) {
            java.io.ByteArrayOutputStream baos = null;
            java.util.zip.GZIPOutputStream gzos = null;
            Base64.OutputStream b64os = null;

            try {
                // GZip -> Base64 -> ByteArray
                baos = new java.io.ByteArrayOutputStream();
                b64os = new Base64.OutputStream(baos, ENCODE | options);
                gzos = new java.util.zip.GZIPOutputStream(b64os);

                gzos.write(source, off, len);
                gzos.close();
            } // end try
            catch (java.io.IOException e) {
                e.printStackTrace();
                return null;
            } // end catch
            finally {
                try {
                    gzos.close();
                } catch (Exception e) {
                }
                try {
                    b64os.close();
                } catch (Exception e) {
                }
                try {
                    baos.close();
                } catch (Exception e) {
                }
            } // end finally

            // Return value according to relevant encoding.
            try {
                return new String(baos.toByteArray(), PREFERRED_ENCODING);
            } // end try
            catch (java.io.UnsupportedEncodingException uue) {
                return new String(baos.toByteArray());
            } // end catch
        } // end if: compress

        // Else, don't compress. Better not to use streams at all then.
        else {
            // Convert option to boolean in way that code likes it.
            boolean breakLines = dontBreakLines == 0;

            int len43 = len * 4 / 3;
            byte[] outBuff = new byte[(len43) // Main 4:3
                    + ((len % 3) > 0 ? 4 : 0) // Account for padding
                    + (breakLines ? (len43 / MAX_LINE_LENGTH) : 0)]; // New lines
            int d = 0;
            int e = 0;
            int len2 = len - 2;
            int lineLength = 0;
            for (; d < len2; d += 3, e += 4) {
                encode3to4(source, d + off, 3, outBuff, e, options);

                lineLength += 4;
                if (breakLines && lineLength == MAX_LINE_LENGTH) {
                    outBuff[e + 4] = NEW_LINE;
                    e++;
                    lineLength = 0;
                } // end if: end of line
            } // en dfor: each piece of array

            if (d < len) {
                encode3to4(source, d + off, len - d, outBuff, e, options);
                e += 4;
            } // end if: some padding needed

            // Return value according to relevant encoding.
            try {
                return new String(outBuff, 0, e, PREFERRED_ENCODING);
            } // end try
            catch (java.io.UnsupportedEncodingException uue) {
                return new String(outBuff, 0, e);
            } // end catch

        } // end else: don't compress

    } // end encodeBytes

	/* D E C O D I N G M E T H O D S */


    private static int decode4to3(byte[] source, int srcOffset, byte[] destination, int destOffset, int options) {
        byte[] DECODABET = getDecodabet(options);

        // Example: Dk==
        if (source[srcOffset + 2] == EQUALS_SIGN) {
            // Two ways to do the same thing. Don't know which way I like best.
            //int outBuff =   ( ( DECODABET[ source[ srcOffset    ] ] << 24 ) >>>  6 )
            //              | ( ( DECODABET[ source[ srcOffset + 1] ] << 24 ) >>> 12 );
            int outBuff = ((DECODABET[source[srcOffset]] & 0xFF) << 18)
                    | ((DECODABET[source[srcOffset + 1]] & 0xFF) << 12);

            destination[destOffset] = (byte) (outBuff >>> 16);
            return 1;
        }

        // Example: DkL=
        else if (source[srcOffset + 3] == EQUALS_SIGN) {
            // Two ways to do the same thing. Don't know which way I like best.
            //int outBuff =   ( ( DECODABET[ source[ srcOffset     ] ] << 24 ) >>>  6 )
            //              | ( ( DECODABET[ source[ srcOffset + 1 ] ] << 24 ) >>> 12 )
            //              | ( ( DECODABET[ source[ srcOffset + 2 ] ] << 24 ) >>> 18 );
            int outBuff = ((DECODABET[source[srcOffset]] & 0xFF) << 18)
                    | ((DECODABET[source[srcOffset + 1]] & 0xFF) << 12)
                    | ((DECODABET[source[srcOffset + 2]] & 0xFF) << 6);

            destination[destOffset] = (byte) (outBuff >>> 16);
            destination[destOffset + 1] = (byte) (outBuff >>> 8);
            return 2;
        }

        // Example: DkLE
        else {
            try {
                // Two ways to do the same thing. Don't know which way I like best.
                //int outBuff =   ( ( DECODABET[ source[ srcOffset     ] ] << 24 ) >>>  6 )
                //              | ( ( DECODABET[ source[ srcOffset + 1 ] ] << 24 ) >>> 12 )
                //              | ( ( DECODABET[ source[ srcOffset + 2 ] ] << 24 ) >>> 18 )
                //              | ( ( DECODABET[ source[ srcOffset + 3 ] ] << 24 ) >>> 24 );
                int outBuff = ((DECODABET[source[srcOffset]] & 0xFF) << 18)
                        | ((DECODABET[source[srcOffset + 1]] & 0xFF) << 12)
                        | ((DECODABET[source[srcOffset + 2]] & 0xFF) << 6)
                        | ((DECODABET[source[srcOffset + 3]] & 0xFF));

                destination[destOffset] = (byte) (outBuff >> 16);
                destination[destOffset + 1] = (byte) (outBuff >> 8);
                destination[destOffset + 2] = (byte) (outBuff);

                return 3;
            } catch (Exception e) {
                System.out.println("" + source[srcOffset] + ": " + (DECODABET[source[srcOffset]]));
                System.out.println("" + source[srcOffset + 1] + ": " + (DECODABET[source[srcOffset + 1]]));
                System.out.println("" + source[srcOffset + 2] + ": " + (DECODABET[source[srcOffset + 2]]));
                System.out.println("" + source[srcOffset + 3] + ": " + (DECODABET[source[srcOffset + 3]]));
                return -1;
            } // end catch
        }
    } // end decodeToBytes


    public static byte[] decode(byte[] source, int off, int len, int options) {
        byte[] DECODABET = getDecodabet(options);

        int len34 = len * 3 / 4;
        byte[] outBuff = new byte[len34]; // Upper limit on size of output
        int outBuffPosn = 0;

        byte[] b4 = new byte[4];
        int b4Posn = 0;
        int i = 0;
        byte sbiCrop = 0;
        byte sbiDecode = 0;
        for (i = off; i < off + len; i++) {
            sbiCrop = (byte) (source[i] & 0x7f); // Only the low seven bits
            sbiDecode = DECODABET[sbiCrop];

            if (sbiDecode >= WHITE_SPACE_ENC) // White space, Equals sign or better
            {
                if (sbiDecode >= EQUALS_SIGN_ENC) {
                    b4[b4Posn++] = sbiCrop;
                    if (b4Posn > 3) {
                        outBuffPosn += decode4to3(b4, 0, outBuff, outBuffPosn, options);
                        b4Posn = 0;

                        // If that was the equals sign, break out of 'for' loop
                        if (sbiCrop == EQUALS_SIGN)
                            break;
                    } // end if: quartet built

                } // end if: equals sign or better

            } // end if: white space, equals sign or better
            else {
                System.err.println("Bad Base64 input character at " + i + ": " + source[i] + "(decimal)");
                return null;
            } // end else:
        } // each input character

        byte[] out = new byte[outBuffPosn];
        System.arraycopy(outBuff, 0, out, 0, outBuffPosn);
        return out;
    } // end decode


    public static byte[] decodePlain(String s) {
        //		byte[] buf;
        //		try {
        //			buf = s.getBytes(PREFERRED_ENCODING);
        //		} catch (UnsupportedEncodingException e) {
        //			buf = s.getBytes();
        //		}
        //		return decode(buf, 0, buf.length, NO_OPTIONS);
        return decode(s);
    }


    public static byte[] decode(String s) {
        return decode(s, NO_OPTIONS);
    }


    public static byte[] decodeAndGunzip(String s) {
        return decode(s, GZIP);
    }


    public static byte[] decode(String s, int options) {
        byte[] bytes;
        try {
            bytes = s.getBytes(PREFERRED_ENCODING);
        } // end try
        catch (java.io.UnsupportedEncodingException uee) {
            bytes = s.getBytes();
        } // end catch
        //</change>

        // Decode
        bytes = decode(bytes, 0, bytes.length, options);

        // Check to see if it's gzip-compressed
        // GZIP Magic Two-Byte Number: 0x8b1f (35615)
        if (((options & GZIP) == GZIP) && bytes != null && bytes.length >= 4) {

            int head = ((int) bytes[0] & 0xff) | ((bytes[1] << 8) & 0xff00);
            if (java.util.zip.GZIPInputStream.GZIP_MAGIC == head) {
                java.io.ByteArrayInputStream bais = null;
                java.util.zip.GZIPInputStream gzis = null;
                java.io.ByteArrayOutputStream baos = null;
                byte[] buffer = new byte[2048];
                int length = 0;

                try {
                    baos = new java.io.ByteArrayOutputStream();
                    bais = new java.io.ByteArrayInputStream(bytes);
                    gzis = new java.util.zip.GZIPInputStream(bais);

                    while ((length = gzis.read(buffer)) >= 0) {
                        baos.write(buffer, 0, length);
                    } // end while: reading input

                    // No error? Get new bytes.
                    bytes = baos.toByteArray();

                } // end try
                catch (java.io.IOException e) {
                    // Just return originally-decoded bytes
                } // end catch
                finally {
                    try {
                        baos.close();
                    } catch (Exception e) {
                    }
                    try {
                        gzis.close();
                    } catch (Exception e) {
                    }
                    try {
                        bais.close();
                    } catch (Exception e) {
                    }
                } // end finally

            } // end if: gzipped
        } // end if: bytes.length >= 2

        return bytes;
    } // end decode


    public static Object decodeToObject(String encodedObject) {
        // Decode and gunzip if necessary
        byte[] objBytes = decode(encodedObject, GZIP);

        java.io.ByteArrayInputStream bais = null;
        java.io.ObjectInputStream ois = null;
        Object obj = null;

        try {
            bais = new java.io.ByteArrayInputStream(objBytes);
            ois = new java.io.ObjectInputStream(bais);

            obj = ois.readObject();
        } // end try
        catch (java.io.IOException e) {
            e.printStackTrace();
            obj = null;
        } // end catch
        catch (java.lang.ClassNotFoundException e) {
            e.printStackTrace();
            obj = null;
        } // end catch
        finally {
            try {
                bais.close();
            } catch (Exception e) {
            }
            try {
                ois.close();
            } catch (Exception e) {
            }
        } // end finally

        return obj;
    } // end decodeObject


    public static Object decodeToObjectFragile(String encodedObject) throws ClassNotFoundException, IOException {
        // Decode and gunzip if necessary
        byte[] objBytes = decode(encodedObject, GZIP);

        java.io.ByteArrayInputStream bais = null;
        java.io.ObjectInputStream ois = null;
        Object obj = null;
        try {
            bais = new java.io.ByteArrayInputStream(objBytes);
            ois = new java.io.ObjectInputStream(bais);
            obj = ois.readObject();
        } finally {
            try {
                bais.close();
            } catch (Exception e) {
            }
            try {
                ois.close();
            } catch (Exception e) {
            }
        } // end finally

        return obj;
    } // end decodeObject


    public static boolean encodeToFile(byte[] dataToEncode, String filename) {
        boolean success = false;
        Base64.OutputStream bos = null;
        try {
            bos = new Base64.OutputStream(new java.io.FileOutputStream(filename), Base64.ENCODE);
            bos.write(dataToEncode);
            success = true;
        } // end try
        catch (java.io.IOException e) {

            success = false;
        } // end catch: IOException
        finally {
            try {
                bos.close();
            } catch (Exception e) {
            }
        } // end finally

        return success;
    } // end encodeToFile


    public static boolean decodeToFile(String dataToDecode, String filename) {
        boolean success = false;
        Base64.OutputStream bos = null;
        try {
            bos = new Base64.OutputStream(new java.io.FileOutputStream(filename), Base64.DECODE);
            bos.write(dataToDecode.getBytes(PREFERRED_ENCODING));
            success = true;
        } // end try
        catch (java.io.IOException e) {
            success = false;
        } // end catch: IOException
        finally {
            try {
                bos.close();
            } catch (Exception e) {
            }
        } // end finally

        return success;
    } // end decodeToFile


    public static byte[] decodeFromFile(String filename) {
        byte[] decodedData = null;
        Base64.InputStream bis = null;
        try {
            // Set up some useful variables
            java.io.File file = new java.io.File(filename);
            byte[] buffer = null;
            int length = 0;
            int numBytes = 0;

            // Check for size of file
            if (file.length() > Integer.MAX_VALUE) {
                System.err.println("File is too big for this convenience method (" + file.length() + " bytes).");
                return null;
            } // end if: file too big for int index
            buffer = new byte[(int) file.length()];

            // Open a stream
            bis = new Base64.InputStream(new java.io.BufferedInputStream(new java.io.FileInputStream(file)),
                    Base64.DECODE);

            // Read until done
            while ((numBytes = bis.read(buffer, length, 4096)) >= 0)
                length += numBytes;

            // Save in a variable to return
            decodedData = new byte[length];
            System.arraycopy(buffer, 0, decodedData, 0, length);

        } // end try
        catch (java.io.IOException e) {
            System.err.println("Error decoding from file " + filename);
        } // end catch: IOException
        finally {
            try {
                bis.close();
            } catch (Exception e) {
            }
        } // end finally

        return decodedData;
    } // end decodeFromFile


    public static String encodeFromFile(String filename) {
        String encodedData = null;
        Base64.InputStream bis = null;
        try {
            // Set up some useful variables
            java.io.File file = new java.io.File(filename);
            byte[] buffer = new byte[Math.max((int) (file.length() * 1.4), 40)]; // Need max() for math on small files (v2.2.1)
            int length = 0;
            int numBytes = 0;

            // Open a stream
            bis = new Base64.InputStream(new java.io.BufferedInputStream(new java.io.FileInputStream(file)),
                    Base64.ENCODE);

            // Read until done
            while ((numBytes = bis.read(buffer, length, 4096)) >= 0)
                length += numBytes;

            // Save in a variable to return
            encodedData = new String(buffer, 0, length, Base64.PREFERRED_ENCODING);

        } // end try
        catch (java.io.IOException e) {
            System.err.println("Error encoding from file " + filename);
        } // end catch: IOException
        finally {
            try {
                bis.close();
            } catch (Exception e) {
            }
        } // end finally

        return encodedData;
    } // end encodeFromFile


    public static void encodeFileToFile(String infile, String outfile) {
        String encoded = Base64.encodeFromFile(infile);
        java.io.OutputStream out = null;
        try {
            out = new java.io.BufferedOutputStream(new java.io.FileOutputStream(outfile));
            out.write(encoded.getBytes("US-ASCII")); // Strict, 7-bit output.
        } // end try
        catch (java.io.IOException ex) {
            ex.printStackTrace();
        } // end catch
        finally {
            try {
                out.close();
            } catch (Exception ex) {
            }
        } // end finally
    } // end encodeFileToFile


    public static void decodeFileToFile(String infile, String outfile) {
        byte[] decoded = Base64.decodeFromFile(infile);
        java.io.OutputStream out = null;
        try {
            out = new java.io.BufferedOutputStream(new java.io.FileOutputStream(outfile));
            out.write(decoded);
        } // end try
        catch (java.io.IOException ex) {
            ex.printStackTrace();
        } // end catch
        finally {
            try {
                out.close();
            } catch (Exception ex) {
            }
        } // end finally
    } // end decodeFileToFile

	/* I N N E R C L A S S I N P U T S T R E A M */

    public static class InputStream extends java.io.FilterInputStream {
        private boolean encode; // Encoding or decoding
        private int position; // Current position in the buffer
        private byte[] buffer; // Small buffer holding converted data
        private int bufferLength; // Length of buffer (3 or 4)
        private int numSigBytes; // Number of meaningful bytes in the buffer
        private int lineLength;
        private boolean breakLines; // Break lines at less than 80 characters
        private int options; // Record options used to create the stream.
        private byte[] alphabet; // Local copies to avoid extra method calls
        private byte[] decodabet; // Local copies to avoid extra method calls


        public InputStream(java.io.InputStream in) {
            this(in, DECODE);
        } // end constructor


        public InputStream(java.io.InputStream in, int options) {
            super(in);
            this.breakLines = (options & DONT_BREAK_LINES) != DONT_BREAK_LINES;
            this.encode = (options & ENCODE) == ENCODE;
            this.bufferLength = encode ? 4 : 3;
            this.buffer = new byte[bufferLength];
            this.position = -1;
            this.lineLength = 0;
            this.options = options; // Record for later, mostly to determine which alphabet to use
            this.alphabet = getAlphabet(options);
            this.decodabet = getDecodabet(options);
        } // end constructor


        @Override
        public int read() throws java.io.IOException {
            // Do we need to get data?
            if (position < 0) {
                if (encode) {
                    byte[] b3 = new byte[3];
                    int numBinaryBytes = 0;
                    for (int i = 0; i < 3; i++) {
                        try {
                            int b = in.read();

                            // If end of stream, b is -1.
                            if (b >= 0) {
                                b3[i] = (byte) b;
                                numBinaryBytes++;
                            } // end if: not end of stream

                        } // end try: read
                        catch (java.io.IOException e) {
                            // Only a problem if we got no data at all.
                            if (i == 0)
                                throw e;

                        } // end catch
                    } // end for: each needed input byte

                    if (numBinaryBytes > 0) {
                        encode3to4(b3, 0, numBinaryBytes, buffer, 0, options);
                        position = 0;
                        numSigBytes = 4;
                    } // end if: got data
                    else {
                        return -1;
                    } // end else
                } // end if: encoding

                // Else decoding
                else {
                    byte[] b4 = new byte[4];
                    int i = 0;
                    for (i = 0; i < 4; i++) {
                        // Read four "meaningful" bytes:
                        int b = 0;
                        do {
                            b = in.read();
                        } while (b >= 0 && decodabet[b & 0x7f] <= WHITE_SPACE_ENC);

                        if (b < 0)
                            break; // Reads a -1 if end of stream

                        b4[i] = (byte) b;
                    } // end for: each needed input byte

                    if (i == 4) {
                        numSigBytes = decode4to3(b4, 0, buffer, 0, options);
                        position = 0;
                    } // end if: got four characters
                    else if (i == 0) {
                        return -1;
                    } // end else if: also padded correctly
                    else {
                        // Must have broken out from above.
                        throw new java.io.IOException("Improperly padded Base64 input.");
                    } // end

                } // end else: decode
            } // end else: get data

            // Got data?
            if (position >= 0) {
                // End of relevant data?
                if ( /* !encode && */position >= numSigBytes)
                    return -1;

                if (encode && breakLines && lineLength >= MAX_LINE_LENGTH) {
                    lineLength = 0;
                    return '\n';
                } // end if
                else {
                    lineLength++; // This isn't important when decoding
                    // but throwing an extra "if" seems
                    // just as wasteful.

                    int b = buffer[position++];

                    if (position >= bufferLength)
                        position = -1;

                    return b & 0xFF; // This is how you "cast" a byte that's
                    // intended to be unsigned.
                } // end else
            } // end if: position >= 0

            // Else error
            else {
                // When JDK1.4 is more accepted, use an assertion here.
                throw new java.io.IOException("Error in Base64 code reading stream.");
            } // end else
        } // end read


        @Override
        public int read(byte[] dest, int off, int len) throws java.io.IOException {
            int i;
            int b;
            for (i = 0; i < len; i++) {
                b = read();

                //if( b < 0 && i == 0 )
                //    return -1;

                if (b >= 0)
                    dest[off + i] = (byte) b;
                else if (i == 0)
                    return -1;
                else
                    break; // Out of 'for' loop
            } // end for: each byte read
            return i;
        } // end read

    } // end inner class InputStream

	/* I N N E R C L A S S O U T P U T S T R E A M */


    public static class OutputStream extends java.io.FilterOutputStream {
        private boolean encode;
        private int position;
        private byte[] buffer;
        private int bufferLength;
        private int lineLength;
        private boolean breakLines;
        private byte[] b4; // Scratch used in a few places
        private boolean suspendEncoding;
        private int options; // Record for later
        private byte[] alphabet; // Local copies to avoid extra method calls
        private byte[] decodabet; // Local copies to avoid extra method calls


        public OutputStream(java.io.OutputStream out) {
            this(out, ENCODE);
        } // end constructor


        public OutputStream(java.io.OutputStream out, int options) {
            super(out);
            this.breakLines = (options & DONT_BREAK_LINES) != DONT_BREAK_LINES;
            this.encode = (options & ENCODE) == ENCODE;
            this.bufferLength = encode ? 3 : 4;
            this.buffer = new byte[bufferLength];
            this.position = 0;
            this.lineLength = 0;
            this.suspendEncoding = false;
            this.b4 = new byte[4];
            this.options = options;
            this.alphabet = getAlphabet(options);
            this.decodabet = getDecodabet(options);
        } // end constructor


        @Override
        public void write(int theByte) throws java.io.IOException {
            // Encoding suspended?
            if (suspendEncoding) {
                super.out.write(theByte);
                return;
            } // end if: supsended

            // Encode?
            if (encode) {
                buffer[position++] = (byte) theByte;
                if (position >= bufferLength) // Enough to encode.
                {
                    out.write(encode3to4(b4, buffer, bufferLength, options));

                    lineLength += 4;
                    if (breakLines && lineLength >= MAX_LINE_LENGTH) {
                        out.write(NEW_LINE);
                        lineLength = 0;
                    } // end if: end of line

                    position = 0;
                } // end if: enough to output
            } // end if: encoding

            // Else, Decoding
            else {
                // Meaningful Base64 character?
                if (decodabet[theByte & 0x7f] > WHITE_SPACE_ENC) {
                    buffer[position++] = (byte) theByte;
                    if (position >= bufferLength) // Enough to output.
                    {
                        int len = Base64.decode4to3(buffer, 0, b4, 0, options);
                        out.write(b4, 0, len);
                        //out.write( Base64.decode4to3( buffer ) );
                        position = 0;
                    } // end if: enough to output
                } // end if: meaningful base64 character
                else if (decodabet[theByte & 0x7f] != WHITE_SPACE_ENC) {
                    throw new java.io.IOException("Invalid character in Base64 data.");
                } // end else: not white space either
            } // end else: decoding
        } // end write


        @Override
        public void write(byte[] theBytes, int off, int len) throws java.io.IOException {
            // Encoding suspended?
            if (suspendEncoding) {
                super.out.write(theBytes, off, len);
                return;
            } // end if: supsended

            for (int i = 0; i < len; i++) {
                write(theBytes[off + i]);
            } // end for: each byte written

        } // end write

        /**
         * Method added by PHIL. [Thanks, PHIL. -Rob] This pads the buffer without closing the stream.
         */
        public void flushBase64() throws java.io.IOException {
            if (position > 0) {
                if (encode) {
                    out.write(encode3to4(b4, buffer, position, options));
                    position = 0;
                } // end if: encoding
                else {
                    throw new java.io.IOException("Base64 input not properly padded.");
                } // end else: decoding
            } // end if: buffer partially full

        } // end flush

        /**
         * Flushes and closes (I think, in the superclass) the stream.
         *
         */
        @Override
        public void close() throws java.io.IOException {
            // 1. Ensure that pending characters are written
            flushBase64();

            // 2. Actually close the stream
            // Base class both flushes and closes.
            super.close();

            buffer = null;
            out = null;
        } // end close

        /**
         * Suspends encoding of the stream. May be helpful if you need to embed a piece of base640-encoded data in a
         * stream.
         *
         */
        public void suspendEncoding() throws java.io.IOException {
            flushBase64();
            this.suspendEncoding = true;
        } // end suspendEncoding

        /**
         * Resumes encoding of the stream. May be helpful if you need to embed a piece of base640-encoded data in a
         * stream.
         *
         */
        public void resumeEncoding() {
            this.suspendEncoding = false;
        } // end resumeEncoding

    } // end inner class OutputStream

} // end class Base64
