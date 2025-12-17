import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import java.nio.charset.StandardCharsets;

public class GOSTArchiver {

    // =============== СТРИБОГ ===============
    private static final byte[] S = {
            (byte)0xFC, (byte)0xEE, (byte)0xDD, 0x11, (byte)0xCF, 0x6E, 0x31, 0x16,
            (byte)0xFB, (byte)0xC4, (byte)0xFA, (byte)0xDA, 0x23, (byte)0xC5, 0x04, 0x4D,
            (byte)0xE9, 0x77, (byte)0xF0, (byte)0xDB, (byte)0x93, 0x2E, (byte)0x99, (byte)0xBA,
            0x17, 0x36, (byte)0xF1, (byte)0xBB, 0x14, (byte)0xCD, 0x5F, (byte)0xC1,
            (byte)0xF9, 0x18, 0x65, 0x5A, (byte)0xE2, 0x5C, (byte)0xEF, 0x21,
            (byte)0x81, 0x1C, 0x3C, 0x42, (byte)0x8B, 0x01, (byte)0x8E, 0x4F,
            0x05, (byte)0x84, 0x02, (byte)0xAE, (byte)0xE3, 0x6A, (byte)0x8F, (byte)0xA0,
            0x06, 0x0B, (byte)0xED, (byte)0x98, 0x7F, (byte)0xD4, (byte)0xD3, 0x1F,
            (byte)0xEB, 0x34, 0x2C, 0x51, (byte)0xEA, (byte)0xC8, 0x48, (byte)0xAB,
            (byte)0xF2, 0x2A, 0x68, (byte)0xA2, (byte)0xFD, 0x3A, (byte)0xCE, (byte)0xCC,
            (byte)0xB5, 0x70, 0x0E, 0x56, 0x08, 0x0C, 0x76, 0x12,
            (byte)0xBF, 0x72, 0x13, 0x47, (byte)0x9C, (byte)0xB7, 0x5D, (byte)0x87,
            0x15, (byte)0xA1, (byte)0x96, 0x29, 0x10, 0x7B, (byte)0x9A, (byte)0xC7,
            (byte)0xF3, (byte)0x91, 0x78, 0x6F, (byte)0x9D, (byte)0x9E, (byte)0xB2, (byte)0xB1,
            0x32, 0x75, 0x19, 0x3D, (byte)0xFF, 0x35, (byte)0x8A, 0x7E,
            0x6D, 0x54, (byte)0xC6, (byte)0x80, (byte)0xC3, (byte)0xBD, 0x0D, 0x57,
            (byte)0xDF, (byte)0xF5, 0x24, (byte)0xA9, 0x3E, (byte)0xA8, (byte)0x43, (byte)0xC9,
            (byte)0xD7, 0x79, (byte)0xD6, (byte)0xF6, 0x7C, 0x22, (byte)0xB9, 0x03,
            (byte)0xE0, 0x0F, (byte)0xEC, (byte)0xDE, 0x7A, (byte)0x94, (byte)0xB0, (byte)0xBC,
            (byte)0xDC, (byte)0xE8, 0x28, 0x50, 0x4E, 0x33, 0x0A, 0x4A,
            (byte)0xA7, (byte)0x97, 0x60, 0x73, 0x1E, 0x00, 0x62, 0x44,
            0x1A, (byte)0xB8, 0x38, (byte)0x82, 0x64, (byte)0x9F, 0x26, 0x41,
            (byte)0xAD, 0x45, 0x46, (byte)0x92, 0x27, 0x5E, 0x55, 0x2F,
            (byte)0x8C, (byte)0xA3, (byte)0xA5, 0x7D, 0x69, (byte)0xD5, (byte)0x95, 0x3B,
            0x07, 0x58, (byte)0xB3, 0x40, (byte)0x86, (byte)0xAC, 0x1D, (byte)0xF7,
            0x30, 0x37, 0x6B, (byte)0xE4, (byte)0x88, (byte)0xD9, (byte)0xE7, (byte)0x89,
            (byte)0xE1, 0x1B, (byte)0x83, 0x49, 0x4C, 0x3F, (byte)0xF8, (byte)0xFE,
            (byte)0x8D, 0x53, (byte)0xAA, (byte)0x90, (byte)0xCA, (byte)0xD8, (byte)0x85, 0x61,
            0x20, 0x71, 0x67, (byte)0xA4, 0x2D, 0x2B, 0x09, 0x5B,
            (byte)0xCB, (byte)0x9B, 0x25, (byte)0xD0, (byte)0xBE, (byte)0xE5, 0x6C, 0x52,
            0x59, (byte)0xA6, 0x74, (byte)0xD2, (byte)0xE6, (byte)0xF4, (byte)0xB4, (byte)0xC0,
            (byte)0xD1, 0x66, (byte)0xAF, (byte)0xC2, 0x39, 0x4B, 0x63, (byte)0xB6
    };

    private static final int[] P = {
            0, 8, 16, 24, 32, 40, 48, 56,
            1, 9, 17, 25, 33, 41, 49, 57,
            2,10,18,26,34,42,50,58,
            3,11,19,27,35,43,51,59,
            4,12,20,28,36,44,52,60,
            5,13,21,29,37,45,53,61,
            6,14,22,30,38,46,54,62,
            7,15,23,31,39,47,55,63
    };

    private static final long[] A = {
            0x8e20faa72ba0b470L, 0x47107ddd9b505a38L, 0xad08b0e0c3282d1cL, 0xd8045870ef14980eL,
            0x6c022c38f90a4c07L, 0x3601161cf205268dL, 0x1b8e0b0e798c13c8L, 0x83478b07b2468764L,
            0xa011d380818e8f40L, 0x5086e740ce47c920L, 0x2843fd2067adea10L, 0x14aff010bdd87508L,
            0x0ad97808d06cb404L, 0x05e23c0468365a02L, 0x8c711e02341b2d01L, 0x46b60f011a83988eL,
            0x90dab52a387ae76fL, 0x486dd4151c3dfdb9L, 0x24b86a840e90f0d2L, 0x125c354207487869L,
            0x092e94218d243cbaL, 0x8a174a9ec8121e5dL, 0x4585254f64090fa0L, 0xaccc9ca9328a8950L,
            0x9d4df05d5f661451L, 0xc0a878a0a1330aa6L, 0x60543c50de970553L, 0x302a1e286fc58ca7L,
            0x18150f14b9ec46ddL, 0x0c84890ad27623e0L, 0x0642ca05693b9f70L, 0x0321658cba93c138L,
            0x86275df09ce8aaa8L, 0x439da0784e745554L, 0xafc0503c273aa42aL, 0xd960281e9d1d5215L,
            0xe230140fc0802984L, 0x71180a8960409a42L, 0xb60c05ca30204d21L, 0x5b068c651810a89eL,
            0x456c34887a3805b9L, 0xac361a443d1c8cd2L, 0x561b0d22900e4669L, 0x2b838811480723baL,
            0x9bcf4486248d9f5dL, 0xc3e9224312c8c1a0L, 0xeffa11af0964ee50L, 0xf97d86d98a327728L,
            0xe4fa2054a80b329cL, 0x727d102a548b194eL, 0x39b008152acb8227L, 0x9258048415eb419dL,
            0x492c024284fbaec0L, 0xaa16012142f35760L, 0x550b8e9e21f7a530L, 0xa48b474f9ef5dc18L,
            0x70a6a56e2440598eL, 0x3853dc371220a247L, 0x1ca76e95091051adL, 0x0edd37c48a08a6d8L,
            0x07e095624504536cL, 0x8d70c431ac02a736L, 0xc83862965601dd1bL, 0x641c314b2b8ee083L
    };

    private static final long[][] L_TAB = new long[8][256];

    static {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 256; j++) {
                long res = 0;
                for (int k = 0; k < 8; k++) {
                    if ((j & (1 << k)) != 0) {
                        res ^= A[i * 8 + (7 - k)];
                    }
                }
                L_TAB[i][j] = res;
            }
        }
    }

    private static byte[] l(byte[] in) {
        byte[] out = new byte[64];
        for (int i = 0; i < 8; i++) {
            long acc = 0;
            for (int j = 0; j < 8; j++) {
                acc ^= L_TAB[j][in[i * 8 + j] & 0xFF];
            }
            for (int j = 0; j < 8; j++) {
                out[i * 8 + j] = (byte) (acc >>> (56 - 8 * j));
            }
        }
        return out;
    }

    private static byte[] p(byte[] in) {
        byte[] out = new byte[64];
        for (int i = 0; i < 64; i++) {
            out[i] = in[P[i]];
        }
        return out;
    }

    private static byte[] s(byte[] in) {
        byte[] out = new byte[64];
        for (int i = 0; i < 64; i++) {
            out[i] = S[in[i] & 0xFF];
        }
        return out;
    }

    private static byte[] add512(byte[] a, byte[] b) {
        byte[] c = new byte[64];
        int carry = 0;
        for (int i = 63; i >= 0; i--) {
            int sum = (a[i] & 0xFF) + (b[i] & 0xFF) + carry;
            c[i] = (byte) sum;
            carry = sum >>> 8;
        }
        return c;
    }

    private static byte[] xor512(byte[] a, byte[] b) {
        byte[] c = new byte[64];
        for (int i = 0; i < 64; i++) {
            c[i] = (byte) (a[i] ^ b[i]);
        }
        return c;
    }

    private static final byte[][] C = {
            { (byte)0xB1, (byte)0x08, (byte)0x5B, (byte)0xDA, (byte)0x1E, (byte)0xCA, (byte)0xDA, (byte)0xE9, (byte)0xEB, (byte)0xCB, (byte)0x2F, (byte)0x81, (byte)0xC0, (byte)0x65, (byte)0x7C, (byte)0x1F,
                    (byte)0x2F, (byte)0x6A, (byte)0x76, (byte)0x43, (byte)0x2E, (byte)0x45, (byte)0xD0, (byte)0x16, (byte)0x71, (byte)0x4E, (byte)0xB8, (byte)0x8D, (byte)0x75, (byte)0x85, (byte)0xC4, (byte)0xFC,
                    (byte)0x4B, (byte)0x7C, (byte)0xE0, (byte)0x91, (byte)0x92, (byte)0x67, (byte)0x69, (byte)0x01, (byte)0xA2, (byte)0x42, (byte)0x2A, (byte)0x08, (byte)0xA4, (byte)0x60, (byte)0xD3, (byte)0x15,
                    (byte)0x05, (byte)0x76, (byte)0x74, (byte)0x36, (byte)0xCC, (byte)0x74, (byte)0x4D, (byte)0x23, (byte)0xDD, (byte)0x80, (byte)0x65, (byte)0x59, (byte)0xF2, (byte)0xA6, (byte)0x45, (byte)0x07 },
            { (byte)0x6F, (byte)0xA3, (byte)0xB5, (byte)0x8A, (byte)0xA9, (byte)0x9D, (byte)0x2F, (byte)0x1A, (byte)0x4F, (byte)0xE3, (byte)0x9D, (byte)0x46, (byte)0x0F, (byte)0x70, (byte)0xB5, (byte)0xD7,
                    (byte)0xF3, (byte)0xFE, (byte)0xEA, (byte)0x72, (byte)0x0A, (byte)0x23, (byte)0x2B, (byte)0x98, (byte)0x61, (byte)0xD5, (byte)0x5E, (byte)0x0F, (byte)0x16, (byte)0xB5, (byte)0x01, (byte)0x31,
                    (byte)0x9A, (byte)0xB5, (byte)0x17, (byte)0x6B, (byte)0x12, (byte)0xD6, (byte)0x99, (byte)0x58, (byte)0x5C, (byte)0xB5, (byte)0x61, (byte)0xC2, (byte)0xDB, (byte)0x0A, (byte)0xA7, (byte)0xCA,
                    (byte)0x55, (byte)0xDD, (byte)0xA2, (byte)0x1B, (byte)0xD7, (byte)0xCB, (byte)0xCD, (byte)0x56, (byte)0xE6, (byte)0x79, (byte)0x04, (byte)0x70, (byte)0x21, (byte)0xB1, (byte)0x9B, (byte)0xB7 },
            { (byte)0xF5, (byte)0x74, (byte)0xDC, (byte)0xAC, (byte)0x2B, (byte)0xCE, (byte)0x2F, (byte)0xC7, (byte)0x0A, (byte)0x39, (byte)0xFC, (byte)0x28, (byte)0x6A, (byte)0x3D, (byte)0x84, (byte)0x35,
                    (byte)0x06, (byte)0xF1, (byte)0x5E, (byte)0x5F, (byte)0x52, (byte)0x9C, (byte)0x1F, (byte)0x8B, (byte)0xF2, (byte)0xEA, (byte)0x75, (byte)0x14, (byte)0xB1, (byte)0x29, (byte)0x7B, (byte)0x7B,
                    (byte)0xD3, (byte)0xE2, (byte)0x0F, (byte)0xE4, (byte)0x90, (byte)0x35, (byte)0x9E, (byte)0xB1, (byte)0xC1, (byte)0xC9, (byte)0x3A, (byte)0x37, (byte)0x60, (byte)0x62, (byte)0xDB, (byte)0x09,
                    (byte)0xC2, (byte)0xB6, (byte)0xF4, (byte)0x43, (byte)0x86, (byte)0x7A, (byte)0xDB, (byte)0x31, (byte)0x99, (byte)0x1E, (byte)0x96, (byte)0xF5, (byte)0x0A, (byte)0xBA, (byte)0x0A, (byte)0xB2 },
            { (byte)0xEF, (byte)0x1F, (byte)0xDF, (byte)0xB3, (byte)0xE8, (byte)0x15, (byte)0x66, (byte)0xD2, (byte)0xF9, (byte)0x48, (byte)0xE1, (byte)0xA0, (byte)0x5D, (byte)0x71, (byte)0xE4, (byte)0xDD,
                    (byte)0x48, (byte)0x8E, (byte)0x85, (byte)0x7E, (byte)0x33, (byte)0x5C, (byte)0x3C, (byte)0x7D, (byte)0x9D, (byte)0x72, (byte)0x1C, (byte)0xAD, (byte)0x68, (byte)0x5E, (byte)0x35, (byte)0x3F,
                    (byte)0xA9, (byte)0xD7, (byte)0x2C, (byte)0x82, (byte)0xED, (byte)0x03, (byte)0xD6, (byte)0x75, (byte)0xD8, (byte)0xB7, (byte)0x13, (byte)0x33, (byte)0x93, (byte)0x52, (byte)0x03, (byte)0xBE,
                    (byte)0x34, (byte)0x53, (byte)0xEA, (byte)0xA1, (byte)0x93, (byte)0xE8, (byte)0x37, (byte)0xF1, (byte)0x22, (byte)0x0C, (byte)0xBE, (byte)0xBC, (byte)0x84, (byte)0xE3, (byte)0xD1, (byte)0x2E },
            { (byte)0x4B, (byte)0xEA, (byte)0x6B, (byte)0xAC, (byte)0xAD, (byte)0x47, (byte)0x47, (byte)0x99, (byte)0x9A, (byte)0x3F, (byte)0x41, (byte)0x0C, (byte)0x6C, (byte)0xA9, (byte)0x23, (byte)0x63,
                    (byte)0x7F, (byte)0x15, (byte)0x1C, (byte)0x1F, (byte)0x16, (byte)0x86, (byte)0x10, (byte)0x4A, (byte)0x35, (byte)0x9E, (byte)0x35, (byte)0xD7, (byte)0x80, (byte)0x0F, (byte)0xFF, (byte)0xBD,
                    (byte)0xBF, (byte)0xCD, (byte)0x17, (byte)0x47, (byte)0x25, (byte)0x3A, (byte)0xF5, (byte)0xA3, (byte)0xDF, (byte)0xFF, (byte)0x00, (byte)0xB7, (byte)0x23, (byte)0x27, (byte)0x1A, (byte)0x16,
                    (byte)0x7A, (byte)0x56, (byte)0xA2, (byte)0x7E, (byte)0xA9, (byte)0xEA, (byte)0x63, (byte)0xF5, (byte)0x60, (byte)0x17, (byte)0x58, (byte)0xFD, (byte)0x7C, (byte)0x6C, (byte)0xFE, (byte)0x57 },
            { (byte)0xAE, (byte)0x4F, (byte)0xAE, (byte)0xAE, (byte)0x1D, (byte)0x3A, (byte)0xD3, (byte)0xD9, (byte)0x6F, (byte)0xA4, (byte)0xC3, (byte)0x3B, (byte)0x7A, (byte)0x30, (byte)0x39, (byte)0xC0,
                    (byte)0x2D, (byte)0x66, (byte)0xC4, (byte)0xF9, (byte)0x51, (byte)0x42, (byte)0xA4, (byte)0x6C, (byte)0x18, (byte)0x7F, (byte)0x9A, (byte)0xB4, (byte)0x9A, (byte)0xF0, (byte)0x8E, (byte)0xC6,
                    (byte)0xCF, (byte)0xFA, (byte)0xA6, (byte)0xB7, (byte)0x1C, (byte)0x9A, (byte)0xB7, (byte)0xB4, (byte)0x0A, (byte)0xF2, (byte)0x1F, (byte)0x66, (byte)0xC2, (byte)0xBE, (byte)0xC6, (byte)0xB6,
                    (byte)0xBF, (byte)0x71, (byte)0xC5, (byte)0x72, (byte)0x36, (byte)0x90, (byte)0x4F, (byte)0x35, (byte)0xFA, (byte)0x68, (byte)0x40, (byte)0x7A, (byte)0x46, (byte)0x64, (byte)0x7D, (byte)0x6E },
            { (byte)0xF4, (byte)0xC7, (byte)0x0E, (byte)0x16, (byte)0xEE, (byte)0xAA, (byte)0xC5, (byte)0xEC, (byte)0x51, (byte)0xAC, (byte)0x86, (byte)0xFE, (byte)0xBF, (byte)0x24, (byte)0x09, (byte)0x54,
                    (byte)0x39, (byte)0x9E, (byte)0xC6, (byte)0xC7, (byte)0xE6, (byte)0xBF, (byte)0x87, (byte)0xC9, (byte)0xD3, (byte)0x47, (byte)0x3E, (byte)0x33, (byte)0x19, (byte)0x7A, (byte)0x93, (byte)0xC9,
                    (byte)0x09, (byte)0x92, (byte)0xAB, (byte)0xC5, (byte)0x2D, (byte)0x82, (byte)0x2C, (byte)0x37, (byte)0x06, (byte)0x47, (byte)0x69, (byte)0x83, (byte)0x28, (byte)0x4A, (byte)0x05, (byte)0x04,
                    (byte)0x35, (byte)0x17, (byte)0x45, (byte)0x4C, (byte)0xA2, (byte)0x3C, (byte)0x4A, (byte)0xF3, (byte)0x88, (byte)0x86, (byte)0x56, (byte)0x4D, (byte)0x3A, (byte)0x14, (byte)0xD4, (byte)0x93 },
            { (byte)0x9B, (byte)0x1F, (byte)0x5B, (byte)0x42, (byte)0x4D, (byte)0x93, (byte)0xC9, (byte)0xA7, (byte)0x03, (byte)0xE7, (byte)0xAA, (byte)0x02, (byte)0x0C, (byte)0x6E, (byte)0x41, (byte)0x41,
                    (byte)0x4E, (byte)0xB7, (byte)0xF8, (byte)0x71, (byte)0x9C, (byte)0x36, (byte)0xDE, (byte)0x1E, (byte)0x89, (byte)0xB4, (byte)0x44, (byte)0x3B, (byte)0x4D, (byte)0xDB, (byte)0xC4, (byte)0x9A,
                    (byte)0xF4, (byte)0x89, (byte)0x2B, (byte)0xCB, (byte)0x92, (byte)0x9B, (byte)0x06, (byte)0x90, (byte)0x69, (byte)0xD1, (byte)0x8D, (byte)0x2B, (byte)0xD1, (byte)0xA5, (byte)0xC4, (byte)0x2F,
                    (byte)0x36, (byte)0xAC, (byte)0xC2, (byte)0x35, (byte)0x59, (byte)0x51, (byte)0xA8, (byte)0xD9, (byte)0xA4, (byte)0x7F, (byte)0x0D, (byte)0xD4, (byte)0xBF, (byte)0x02, (byte)0xE7, (byte)0x1E },
            { (byte)0x37, (byte)0x8F, (byte)0x5A, (byte)0x54, (byte)0x16, (byte)0x31, (byte)0x22, (byte)0x9B, (byte)0x94, (byte)0x4C, (byte)0x9A, (byte)0xD8, (byte)0xEC, (byte)0x16, (byte)0x5F, (byte)0xDE,
                    (byte)0x3A, (byte)0x7D, (byte)0x3A, (byte)0x1B, (byte)0x25, (byte)0x89, (byte)0x42, (byte)0x24, (byte)0x3C, (byte)0xD9, (byte)0x55, (byte)0xB7, (byte)0xE0, (byte)0x0D, (byte)0x09, (byte)0x84,
                    (byte)0x80, (byte)0x0A, (byte)0x44, (byte)0x0B, (byte)0xDB, (byte)0xB2, (byte)0xCE, (byte)0xB1, (byte)0x7B, (byte)0x2B, (byte)0x8A, (byte)0x9A, (byte)0xA6, (byte)0x07, (byte)0x9C, (byte)0x54,
                    (byte)0x0E, (byte)0x38, (byte)0xDC, (byte)0x92, (byte)0xCB, (byte)0x1F, (byte)0x2A, (byte)0x60, (byte)0x72, (byte)0x61, (byte)0x44, (byte)0x51, (byte)0x83, (byte)0x23, (byte)0x5A, (byte)0xDB },
            { (byte)0xAB, (byte)0xBE, (byte)0xDE, (byte)0xA6, (byte)0x80, (byte)0x05, (byte)0x6F, (byte)0x52, (byte)0x38, (byte)0x2A, (byte)0xE5, (byte)0x48, (byte)0xB2, (byte)0xE4, (byte)0xF3, (byte)0xF3,
                    (byte)0x89, (byte)0x41, (byte)0xE7, (byte)0x1C, (byte)0xFF, (byte)0x8A, (byte)0x78, (byte)0xDB, (byte)0x1F, (byte)0xFF, (byte)0xE1, (byte)0x8A, (byte)0x1B, (byte)0x33, (byte)0x61, (byte)0x03,
                    (byte)0x9F, (byte)0xE7, (byte)0x67, (byte)0x02, (byte)0xAF, (byte)0x69, (byte)0x33, (byte)0x4B, (byte)0x7A, (byte)0x1E, (byte)0x6C, (byte)0x30, (byte)0x3B, (byte)0x76, (byte)0x52, (byte)0xF4,
                    (byte)0x36, (byte)0x98, (byte)0xFA, (byte)0xD1, (byte)0x15, (byte)0x3B, (byte)0xB6, (byte)0xC3, (byte)0x74, (byte)0xB4, (byte)0xC7, (byte)0xFB, (byte)0x98, (byte)0x45, (byte)0x9C, (byte)0xED },
            { (byte)0x7B, (byte)0xCD, (byte)0x9E, (byte)0xD0, (byte)0xEF, (byte)0xC8, (byte)0x89, (byte)0xFB, (byte)0x30, (byte)0x02, (byte)0xC6, (byte)0xCD, (byte)0x63, (byte)0x5A, (byte)0xFE, (byte)0x94,
                    (byte)0xD8, (byte)0xFA, (byte)0x6B, (byte)0xBB, (byte)0xEB, (byte)0xAB, (byte)0x07, (byte)0x61, (byte)0x20, (byte)0x01, (byte)0x80, (byte)0x21, (byte)0x14, (byte)0x84, (byte)0x66, (byte)0x79,
                    (byte)0x8A, (byte)0x1D, (byte)0x71, (byte)0xEF, (byte)0xEA, (byte)0x48, (byte)0xB9, (byte)0xCA, (byte)0xEF, (byte)0xBA, (byte)0xCD, (byte)0x1D, (byte)0x7D, (byte)0x47, (byte)0x6E, (byte)0x98,
                    (byte)0xDE, (byte)0xA2, (byte)0x59, (byte)0x4A, (byte)0xC0, (byte)0x6F, (byte)0xD8, (byte)0x5D, (byte)0x6B, (byte)0xCA, (byte)0xA4, (byte)0xCD, (byte)0x81, (byte)0xF3, (byte)0x2D, (byte)0x1B },
            { (byte)0x37, (byte)0x8E, (byte)0xE7, (byte)0x67, (byte)0xF1, (byte)0x16, (byte)0x31, (byte)0xBA, (byte)0xD2, (byte)0x13, (byte)0x80, (byte)0xB0, (byte)0x04, (byte)0x49, (byte)0xB1, (byte)0x7A,
                    (byte)0xCD, (byte)0xA4, (byte)0x3C, (byte)0x32, (byte)0xBC, (byte)0xDF, (byte)0x1D, (byte)0x77, (byte)0xF8, (byte)0x20, (byte)0x12, (byte)0xD4, (byte)0x30, (byte)0x21, (byte)0x9F, (byte)0x9B,
                    (byte)0x5D, (byte)0x80, (byte)0xEF, (byte)0x9D, (byte)0x18, (byte)0x91, (byte)0xCC, (byte)0x86, (byte)0xE7, (byte)0x1D, (byte)0xA4, (byte)0xAA, (byte)0x88, (byte)0xE1, (byte)0x28, (byte)0x52,
                    (byte)0xFA, (byte)0xF4, (byte)0x17, (byte)0xD5, (byte)0xD9, (byte)0xB2, (byte)0x1B, (byte)0x99, (byte)0x48, (byte)0xBC, (byte)0x92, (byte)0x4A, (byte)0xF1, (byte)0x1B, (byte)0xD7, (byte)0x20 }
    };

    private static byte[] keySchedule(byte[] K, int i) {
        byte[] res = xor512(K, C[i]);
        res = s(res);
        res = p(res);
        res = l(res);
        return res;
    }

    private static byte[] E(byte[] K, byte[] m) {
        byte[] state = xor512(K, m);
        byte[] key = K.clone();
        for (int i = 0; i < 12; i++) {
            state = s(state);
            state = p(state);
            state = l(state);
            key = keySchedule(key, i);
            state = xor512(state, key);
        }
        return state;
    }

    private static byte[] step(byte[] N, byte[] H, byte[] M) {
        byte[] K = xor512(H, N);
        K = s(K);
        K = p(K);
        K = l(K);
        byte[] t = E(K, M);
        t = xor512(t, H);
        return xor512(t, M);
    }

    public static byte[] streebog512(byte[] msg) {
        byte[] H = new byte[64];
        byte[] N = new byte[64];
        byte[] Sigma = new byte[64];

        int len = msg.length;
        int blocks = (len + 1 + 8 + 63) / 64;
        int paddedLen = blocks * 64;
        byte[] padded = new byte[paddedLen];
        System.arraycopy(msg, 0, padded, 0, len);
        padded[len] = 0x01;
        long bitLen = (long) len * 8;
        for (int i = 0; i < 8; i++) {
            padded[paddedLen - 8 + i] = (byte) (bitLen >>> (56 - i * 8));
        }

        for (int i = 0; i < blocks; i++) {
            byte[] block = new byte[64];
            System.arraycopy(padded, i * 64, block, 0, 64);
            H = step(N, H, block);
            byte[] N512 = new byte[64];
            N512[62] = 0x02;
            N = add512(N, N512);
            Sigma = add512(Sigma, block);
        }

        byte[] N0 = new byte[64];
        H = step(N0, H, N);
        H = step(N0, H, Sigma);
        return H;
    }

    // =============== КУЗНЕЧИК ===============
    static final int BLOCK_SIZE = 16;
    static final byte[] Pi;
    static final byte[] reverse_Pi;
    static final byte[] l_vec;
    static byte[][] iter_C = new byte[32][16];
    static byte[][] iter_key = new byte[10][];

    static {
        Pi = new byte[]{
                (byte) 0xFC, (byte) 0xEE, (byte) 0xDD, 0x11, (byte) 0xCF, 0x6E, 0x31, 0x16,
                (byte) 0xFB, (byte) 0xC4, (byte) 0xFA, (byte) 0xDA, 0x23, (byte) 0xC5, 0x04, 0x4D,
                (byte) 0xE9, 0x77, (byte) 0xF0, (byte) 0xDB, (byte) 0x93, 0x2E, (byte) 0x99, (byte) 0xBA,
                0x17, 0x36, (byte) 0xF1, (byte) 0xBB, 0x14, (byte) 0xCD, 0x5F, (byte) 0xC1,
                (byte) 0xF9, 0x18, 0x65, 0x5A, (byte) 0xE2, 0x5C, (byte) 0xEF, 0x21,
                (byte) 0x81, 0x1C, 0x3C, 0x42, (byte) 0x8B, 0x01, (byte) 0x8E, 0x4F,
                0x05, (byte) 0x84, 0x02, (byte) 0xAE, (byte) 0xE3, 0x6A, (byte) 0x8F, (byte) 0xA0,
                0x06, 0x0B, (byte) 0xED, (byte) 0x98, 0x7F, (byte) 0xD4, (byte) 0xD3, 0x1F,
                (byte) 0xEB, 0x34, 0x2C, 0x51, (byte) 0xEA, (byte) 0xC8, 0x48, (byte) 0xAB,
                (byte) 0xF2, 0x2A, 0x68, (byte) 0xA2, (byte) 0xFD, 0x3A, (byte) 0xCE, (byte) 0xCC,
                (byte) 0xB5, 0x70, 0x0E, 0x56, 0x08, 0x0C, 0x76, 0x12,
                (byte) 0xBF, 0x72, 0x13, 0x47, (byte) 0x9C, (byte) 0xB7, 0x5D, (byte) 0x87,
                0x15, (byte) 0xA1, (byte) 0x96, 0x29, 0x10, 0x7B, (byte) 0x9A, (byte) 0xC7,
                (byte) 0xF3, (byte) 0x91, 0x78, 0x6F, (byte) 0x9D, (byte) 0x9E, (byte) 0xB2, (byte) 0xB1,
                0x32, 0x75, 0x19, 0x3D, (byte) 0xFF, 0x35, (byte) 0x8A, 0x7E,
                0x6D, 0x54, (byte) 0xC6, (byte) 0x80, (byte) 0xC3, (byte) 0xBD, 0x0D, 0x57,
                (byte) 0xDF, (byte) 0xF5, 0x24, (byte) 0xA9, 0x3E, (byte) 0xA8, (byte) 0x43, (byte) 0xC9,
                (byte) 0xD7, 0x79, (byte) 0xD6, (byte) 0xF6, 0x7C, 0x22, (byte) 0xB9, 0x03,
                (byte) 0xE0, 0x0F, (byte) 0xEC, (byte) 0xDE, 0x7A, (byte) 0x94, (byte) 0xB0, (byte) 0xBC,
                (byte) 0xDC, (byte) 0xE8, 0x28, 0x50, 0x4E, 0x33, 0x0A, 0x4A,
                (byte) 0xA7, (byte) 0x97, 0x60, 0x73, 0x1E, 0x00, 0x62, 0x44,
                0x1A, (byte) 0xB8, 0x38, (byte) 0x82, 0x64, (byte) 0x9F, 0x26, 0x41,
                (byte) 0xAD, 0x45, 0x46, (byte) 0x92, 0x27, 0x5E, 0x55, 0x2F,
                (byte) 0x8C, (byte) 0xA3, (byte) 0xA5, 0x7D, 0x69, (byte) 0xD5, (byte) 0x95, 0x3B,
                0x07, 0x58, (byte) 0xB3, 0x40, (byte) 0x86, (byte) 0xAC, 0x1D, (byte) 0xF7,
                0x30, 0x37, 0x6B, (byte) 0xE4, (byte) 0x88, (byte) 0xD9, (byte) 0xE7, (byte) 0x89,
                (byte) 0xE1, 0x1B, (byte) 0x83, 0x49, 0x4C, 0x3F, (byte) 0xF8, (byte) 0xFE,
                (byte) 0x8D, 0x53, (byte) 0xAA, (byte) 0x90, (byte) 0xCA, (byte) 0xD8, (byte) 0x85, 0x61,
                0x20, 0x71, 0x67, (byte) 0xA4, 0x2D, 0x2B, 0x09, 0x5B,
                (byte) 0xCB, (byte) 0x9B, 0x25, (byte) 0xD0, (byte) 0xBE, (byte) 0xE5, 0x6C, 0x52,
                0x59, (byte) 0xA6, 0x74, (byte) 0xD2, (byte) 0xE6, (byte) 0xF4, (byte) 0xB4, (byte) 0xC0,
                (byte) 0xD1, 0x66, (byte) 0xAF, (byte) 0xC2, 0x39, 0x4B, 0x63, (byte) 0xB6
        };

        reverse_Pi = new byte[]{
                (byte) 0xA5, 0x2D, 0x32, (byte) 0x8F, 0x0E, 0x30, 0x38, (byte) 0xC0,
                0x54, (byte) 0xE6, (byte) 0x9E, 0x39, 0x55, 0x7E, 0x52, (byte) 0x91,
                0x64, 0x03, 0x57, 0x5A, 0x1C, 0x60, 0x07, 0x18,
                0x21, 0x72, (byte) 0xA8, (byte) 0xD1, 0x29, (byte) 0xC6, (byte) 0xA4, 0x3F,
                (byte) 0xE0, 0x27, (byte) 0x8D, 0x0C, (byte) 0x82, (byte) 0xEA, (byte) 0xAE, (byte) 0xB4,
                (byte) 0x9A, 0x63, 0x49, (byte) 0xE5, 0x42, (byte) 0xE4, 0x15, (byte) 0xB7,
                (byte) 0xC8, 0x06, 0x70, (byte) 0x9D, 0x41, 0x75, 0x19, (byte) 0xC9,
                (byte) 0xAA, (byte) 0xFC, 0x4D, (byte) 0xBF, 0x2A, 0x73, (byte) 0x84, (byte) 0xD5,
                (byte) 0xC3, (byte) 0xAF, 0x2B, (byte) 0x86, (byte) 0xA7, (byte) 0xB1, (byte) 0xB2, 0x5B,
                0x46, (byte) 0xD3, (byte) 0x9F, (byte) 0xFD, (byte) 0xD4, 0x0F, (byte) 0x9C, 0x2F,
                (byte) 0x9B, 0x43, (byte) 0xEF, (byte) 0xD9, 0x79, (byte) 0xB6, 0x53, 0x7F,
                (byte) 0xC1, (byte) 0xF0, 0x23, (byte) 0xE7, 0x25, 0x5E, (byte) 0xB5, 0x1E,
                (byte) 0xA2, (byte) 0xDF, (byte) 0xA6, (byte) 0xFE, (byte) 0xAC, 0x22, (byte) 0xF9, (byte) 0xE2,
                0x4A, (byte) 0xBC, 0x35, (byte) 0xCA, (byte) 0xEE, 0x78, 0x05, 0x6B,
                0x51, (byte) 0xE1, 0x59, (byte) 0xA3, (byte) 0xF2, 0x71, 0x56, 0x11,
                0x6A, (byte) 0x89, (byte) 0x94, 0x65, (byte) 0x8C, (byte) 0xBB, 0x77, 0x3C,
                0x7B, 0x28, (byte) 0xAB, (byte) 0xD2, 0x31, (byte) 0xDE, (byte) 0xC4, 0x5F,
                (byte) 0xCC, (byte) 0xCF, 0x76, 0x2C, (byte) 0xB8, (byte) 0xD8, 0x2E, 0x36,
                (byte) 0xDB, 0x69, (byte) 0xB3, 0x14, (byte) 0x95, (byte) 0xBE, 0x62, (byte) 0xA1,
                0x3B, 0x16, 0x66, (byte) 0xE9, 0x5C, 0x6C, 0x6D, (byte) 0xAD,
                0x37, 0x61, 0x4B, (byte) 0xB9, (byte) 0xE3, (byte) 0xBA, (byte) 0xF1, (byte) 0xA0,
                (byte) 0x85, (byte) 0x83, (byte) 0xDA, 0x47, (byte) 0xC5, (byte) 0xB0, 0x33, (byte) 0xFA,
                (byte) 0x96, 0x6F, 0x6E, (byte) 0xC2, (byte) 0xF6, 0x50, (byte) 0xFF, 0x5D,
                (byte) 0xA9, (byte) 0x8E, 0x17, 0x1B, (byte) 0x97, 0x7D, (byte) 0xEC, 0x58,
                (byte) 0xF7, 0x1F, (byte) 0xFB, 0x7C, 0x09, 0x0D, 0x7A, 0x67,
                0x45, (byte) 0x87, (byte) 0xDC, (byte) 0xE8, 0x4F, 0x1D, 0x4E, 0x04,
                (byte) 0xEB, (byte) 0xF8, (byte) 0xF3, 0x3E, 0x3D, (byte) 0xBD, (byte) 0x8A, (byte) 0x88,
                (byte) 0xDD, (byte) 0xCD, 0x0B, 0x13, (byte) 0x98, 0x02, (byte) 0x93, (byte) 0x80,
                (byte) 0x90, (byte) 0xD0, 0x24, 0x34, (byte) 0xCB, (byte) 0xED, (byte) 0xF4, (byte) 0xCE,
                (byte) 0x99, 0x10, 0x44, 0x40, (byte) 0x92, 0x3A, 0x01, 0x26,
                0x12, 0x1A, 0x48, 0x68, (byte) 0xF5, (byte) 0x81, (byte) 0x8B, (byte) 0xC7,
                (byte) 0xD6, 0x20, 0x0A, 0x08, 0x00, 0x4C, (byte) 0xD7, 0x74
        };

        l_vec = new byte[]{
                1, (byte) 148, 32, (byte) 133, 16, (byte) 194, (byte) 192, 1,
                (byte) 251, 1, (byte) 192, (byte) 194, 16, (byte) 133, 32, (byte) 148
        };
    }

    static private byte[] GOST_Kuz_X(byte[] a, byte[] b) {
        byte[] c = new byte[BLOCK_SIZE];
        for (int i = 0; i < BLOCK_SIZE; i++)
            c[i] = (byte) (a[i] ^ b[i]);
        return c;
    }

    static private byte[] GOST_Kuz_S(byte[] in_data) {
        byte[] out_data = new byte[BLOCK_SIZE];
        for (int i = 0; i < BLOCK_SIZE; i++) {
            int idx = in_data[i] & 0xFF;
            if (idx < 0 || idx >= Pi.length) {
                throw new IllegalArgumentException("Некорректный индекс в S-блоке: " + idx + " в позиции " + i);
            }
            out_data[i] = Pi[idx];
        }
        return out_data;
    }

    static private byte GOST_Kuz_GF_mul(byte a, byte b) {
        byte c = 0;
        for (int i = 0; i < 8; i++) {
            if ((b & 1) == 1) c ^= a;
            boolean hi = (a & 0x80) != 0;
            a <<= 1;
            if (hi) a ^= 0xC3;
            b >>= 1;
        }
        return c;
    }

    static private byte[] GOST_Kuz_R(byte[] state) {
        byte[] internal = new byte[16];
        byte a_15 = 0;
        for (int i = 15; i >= 0; i--) {
            if (i == 0) internal[15] = state[i];
            else internal[i - 1] = state[i];
            a_15 ^= GOST_Kuz_GF_mul(state[i], l_vec[i]);
        }
        internal[15] = a_15;
        return internal;
    }

    static private byte[] GOST_Kuz_L(byte[] in_data) {
        byte[] internal = in_data;
        for (int i = 0; i < 16; i++)
            internal = GOST_Kuz_R(internal);
        return internal;
    }

    static private byte[] GOST_Kuz_reverse_S(byte[] in_data) {
        byte[] out_data = new byte[BLOCK_SIZE];
        for (int i = 0; i < BLOCK_SIZE; i++) {
            int idx = in_data[i] & 0xFF;
            if (idx < 0 || idx >= reverse_Pi.length) {
                throw new IllegalArgumentException("Некорректный индекс в S-блоке: " + idx + " в позиции " + i);
            }
            out_data[i] = reverse_Pi[idx];
        }
        return out_data;
    }

    static private byte[] GOST_Kuz_reverse_R(byte[] state) {
        byte a_0 = state[15];
        byte[] internal = new byte[16];
        for (int i = 1; i < 16; i++) {
            internal[i] = state[i - 1];
            a_0 ^= GOST_Kuz_GF_mul(internal[i], l_vec[i]);
        }
        internal[0] = a_0;
        return internal;
    }

    static private byte[] GOST_Kuz_reverse_L(byte[] in_data) {
        byte[] internal = in_data;
        for (int i = 0; i < 16; i++)
            internal = GOST_Kuz_reverse_R(internal);
        return internal;
    }

    static private void GOST_Kuz_Get_C() {
        for (int i = 0; i < 32; i++) {
            byte[] num = new byte[16];
            num[0] = (byte) (i + 1);
            iter_C[i] = GOST_Kuz_L(num);
        }
    }

    static private byte[][] GOST_Kuz_F(byte[] k1, byte[] k2, byte[] c) {
        byte[] out2 = k1;
        byte[] t = GOST_Kuz_X(k1, c);
        t = GOST_Kuz_S(t);
        t = GOST_Kuz_L(t);
        byte[] out1 = GOST_Kuz_X(t, k2);
        return new byte[][]{out1, out2};
    }

    public static void GOST_Kuz_Expand_Key(byte[] key1, byte[] key2) {
        GOST_Kuz_Get_C();
        iter_key[0] = key1;
        iter_key[1] = key2;
        byte[][] state = {key1, key2};
        for (int r = 0; r < 4; r++) {
            for (int i = 0; i < 8; i++) {
                state = GOST_Kuz_F(state[0], state[1], iter_C[r * 8 + i]);
            }
            iter_key[2 * r + 2] = state[0];
            iter_key[2 * r + 3] = state[1];
        }
    }

    public static byte[] GOST_Kuz_Encrypt_Block(byte[] block) {
        byte[] data = block.clone();
        for (int i = 0; i < 9; i++) {
            data = GOST_Kuz_X(iter_key[i], data);
            data = GOST_Kuz_S(data);
            data = GOST_Kuz_L(data);
        }
        return GOST_Kuz_X(data, iter_key[9]);
    }

    public static byte[] GOST_Kuz_Decrypt_Block(byte[] block) {
        byte[] data = block.clone();
        data = GOST_Kuz_X(data, iter_key[9]);
        for (int i = 8; i >= 0; i--) {
            data = GOST_Kuz_reverse_L(data);
            data = GOST_Kuz_reverse_S(data);
            data = GOST_Kuz_X(iter_key[i], data);
        }
        return data;
    }

    public static byte[] pad(byte[] data) {
        int padLen = BLOCK_SIZE - (data.length % BLOCK_SIZE);
        byte[] padded = new byte[data.length + padLen];
        System.arraycopy(data, 0, padded, 0, data.length);
        for (int i = data.length; i < padded.length; i++)
            padded[i] = (byte) padLen;
        return padded;
    }

    public static byte[] unpad(byte[] data) {
        int padLen = data[data.length - 1] & 0xFF;
        if (padLen < 1 || padLen > BLOCK_SIZE) return data;
        return Arrays.copyOf(data, data.length - padLen);
    }

    public static byte[] encrypt(byte[] plaintext) {
        byte[] padded = pad(plaintext);
        byte[] ciphertext = new byte[padded.length];
        for (int i = 0; i < padded.length; i += BLOCK_SIZE) {
            byte[] block = Arrays.copyOfRange(padded, i, i + BLOCK_SIZE);
            byte[] encrypted = GOST_Kuz_Encrypt_Block(block);
            System.arraycopy(encrypted, 0, ciphertext, i, BLOCK_SIZE);
        }
        return ciphertext;
    }

    public static byte[] decrypt(byte[] ciphertext) {
        byte[] plaintext = new byte[ciphertext.length];
        for (int i = 0; i < ciphertext.length; i += BLOCK_SIZE) {
            byte[] block = Arrays.copyOfRange(ciphertext, i, i + BLOCK_SIZE);
            byte[] decrypted = GOST_Kuz_Decrypt_Block(block);
            System.arraycopy(decrypted, 0, plaintext, i, BLOCK_SIZE);
        }
        return unpad(plaintext);
    }

    // =============== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ===============
    private static byte[] shortToBytes(short value) {
        return new byte[]{(byte)(value >>> 8), (byte)value};
    }
    private static short bytesToShort(byte[] b) {
        return (short)((b[0] << 8) + (b[1] & 0xFF));
    }
    private static byte[] intToBytes(int value) {
        return new byte[]{
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value
        };
    }
    private static int bytesToInt(byte[] b) {
        return (b[0] & 0xFF) << 24 |
                (b[1] & 0xFF) << 16 |
                (b[2] & 0xFF) << 8 |
                (b[3] & 0xFF);
    }
    private static byte[] longToBytes(long value) {
        return new byte[]{
                (byte)(value >>> 56), (byte)(value >>> 48), (byte)(value >>> 40), (byte)(value >>> 32),
                (byte)(value >>> 24), (byte)(value >>> 16), (byte)(value >>> 8),  (byte)value
        };
    }
    private static long bytesToLong(byte[] b) {
        return ((long)(b[0] & 0xFF) << 56) |
                ((long)(b[1] & 0xFF) << 48) |
                ((long)(b[2] & 0xFF) << 40) |
                ((long)(b[3] & 0xFF) << 32) |
                ((long)(b[4] & 0xFF) << 24) |
                ((long)(b[5] & 0xFF) << 16) |
                ((long)(b[6] & 0xFF) << 8)  |
                ((long)(b[7] & 0xFF));
    }

    private static byte[] readExact(InputStream in, int len) throws IOException {
        byte[] buf = new byte[len];
        int total = 0;
        while (total < len) {
            int n = in.read(buf, total, len - total);
            if (n == -1) {
                throw new EOFException("Неожиданный конец данных. Ожидалось " + len + " байт, но получено " + total);
            }
            total += n;
        }
        return buf;
    }

    // =============== СЖАТИЕ ===============
    private static byte[] compress(byte[] data) {
        Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
        deflater.setInput(data);
        deflater.finish();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[8192];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        deflater.end();
        return outputStream.toByteArray();
    }

    private static byte[] decompress(byte[] data) throws IOException {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[8192];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
        } catch (java.util.zip.DataFormatException e) {
            throw new IOException("Ошибка распаковки", e);
        } finally {
            inflater.end();
        }
        return outputStream.toByteArray();
    }

    // =============== АРХИВАЦИЯ ===============
    private static byte[] packFiles(List<FileEntry> entries) throws IOException {
        ByteArrayOutputStream metaOut = new ByteArrayOutputStream();
        ByteArrayOutputStream dataOut = new ByteArrayOutputStream();

        metaOut.write(intToBytes(entries.size()));

        long totalDataSize = 0;
        for (FileEntry entry : entries) {
            byte[] pathBytes = entry.path.getBytes(StandardCharsets.UTF_8);
            if (pathBytes.length > 65535) {
                throw new IOException("Слишком длинный путь: " + entry.path);
            }

            metaOut.write(shortToBytes((short) pathBytes.length));
            metaOut.write(pathBytes);

            metaOut.write(longToBytes(entry.data.length));

            dataOut.write(entry.data);
            totalDataSize += entry.data.length;

            if (totalDataSize < 0) {
                throw new IOException("Общий размер данных превышает допустимый лимит");
            }
        }

        ByteArrayOutputStream full = new ByteArrayOutputStream();
        byte[] metaBytes = metaOut.toByteArray();
        byte[] dataBytes = dataOut.toByteArray();


        full.write(intToBytes(metaBytes.length));
        full.write(metaBytes);
        full.write(dataBytes);

        return full.toByteArray();
    }

    static class FileEntry {
        String path;
        byte[] data;
        FileEntry(String path, byte[] data) {
            this.path = path;
            this.data = data;
        }
    }

    private static void collectFiles(Path root, Path file, List<FileEntry> fileList) throws IOException {
        if (Files.isDirectory(file)) {
            Files.walkFileTree(file, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path f, BasicFileAttributes attrs) throws IOException {
                    String relPath = root.relativize(f).toString().replace('\\', '/');
                    fileList.add(new FileEntry(relPath, Files.readAllBytes(f)));
                    return FileVisitResult.CONTINUE;
                }
            });
        } else {
            String relPath = root.relativize(file).toString().replace('\\', '/');
            fileList.add(new FileEntry(relPath, Files.readAllBytes(file)));
        }
    }

    // =============== РАСПАКОВКА ===============
    private static void unpackFiles(byte[] packed, Path outputDir) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(packed);


        int metaSize = bytesToInt(readExact(in, 4));
        if (metaSize <= 0 || metaSize > packed.length - 4) {
            throw new IOException("Некорректный размер метаданных: " + metaSize);
        }

        byte[] metaData = readExact(in, metaSize);
        ByteArrayInputStream metaIn = new ByteArrayInputStream(metaData);

        int fileCount = bytesToInt(readExact(metaIn, 4));
        if (fileCount < 0 || fileCount > 100000) {
            throw new IOException("Некорректное количество файлов: " + fileCount);
        }

        for (int i = 0; i < fileCount; i++) {
            short pathLen = bytesToShort(readExact(metaIn, 2));
            if (pathLen <= 0 || pathLen > 65535) {
                throw new IOException("Некорректная длина пути: " + pathLen);
            }

            byte[] pathBytes = readExact(metaIn, pathLen);
            String pathStr = new String(pathBytes, StandardCharsets.UTF_8);

            long dataSize = bytesToLong(readExact(metaIn, 8));
            if (dataSize < 0 || dataSize > Integer.MAX_VALUE) {
                throw new IOException("Некорректный размер файла: " + pathStr + " - " + dataSize);
            }

            byte[] data = readExact(in, (int) dataSize);

            Path target = outputDir.resolve(pathStr).normalize();
            if (!target.startsWith(outputDir.normalize())) {
                throw new IOException("Попытка выхода за пределы целевой папки: " + pathStr);
            }
            Files.createDirectories(target.getParent());
            Files.write(target, data);
        }


        if (metaIn.available() > 0) {
            throw new IOException("В метаданных остались непрочитанные данные");
        }


        if (in.available() > 0) {
            throw new IOException("В архиве остались непрочитанные данные");
        }
    }

    // =============== ОСНОВНЫЕ МЕТОДЫ ===============
    private static void archiveAndEncrypt(List<Path> inputPaths, String password, Path outputPath) throws Exception {
        System.out.println("Начинаем архивацию...");


        Path commonRoot = null;
        for (Path input : inputPaths) {
            Path absPath = input.toAbsolutePath();
            if (commonRoot == null) {
                commonRoot = absPath.getParent();
            } else {
                while (!absPath.startsWith(commonRoot)) {
                    commonRoot = commonRoot.getParent();
                    if (commonRoot == null) {
                        commonRoot = Paths.get("");
                        break;
                    }
                }
            }
        }
        if (commonRoot == null) commonRoot = Paths.get("");

        System.out.println("Общий корень: " + commonRoot);

        List<FileEntry> allFiles = new ArrayList<>();
        for (Path input : inputPaths) {
            System.out.println("Обрабатываем: " + input);
            collectFiles(commonRoot, input.toAbsolutePath(), allFiles);
        }

        System.out.println("Найдено файлов: " + allFiles.size());

        byte[] packed = packFiles(allFiles);
        System.out.println("Размер упакованных данных: " + packed.length + " байт");

        byte[] compressed = compress(packed);
        System.out.println("Размер после сжатия: " + compressed.length + " байт");

        byte[] keyMaterial = streebog512(password.getBytes(StandardCharsets.UTF_8));
        byte[] key1 = Arrays.copyOfRange(keyMaterial, 0, 16);
        byte[] key2 = Arrays.copyOfRange(keyMaterial, 16, 32);
        GOST_Kuz_Expand_Key(key1, key2);

        byte[] encrypted = encrypt(compressed);
        System.out.println("Размер после шифрования: " + encrypted.length + " байт");

        byte[] mac = streebog512(encrypted);

        try (FileOutputStream fos = new FileOutputStream(outputPath.toFile())) {
            fos.write("GOST".getBytes());
            fos.write(mac);
            fos.write(encrypted);
        }
        System.out.println("Архив успешно создан: " + outputPath);
        System.out.println("Общий размер архива: " + (4 + 64 + encrypted.length) + " байт");
    }

    private static void decryptAndExtract(Path archivePath, String password, Path outputDir) throws Exception {
        System.out.println("Начинаем распаковку...");
        System.out.println("Архив: " + archivePath);

        byte[] data = Files.readAllBytes(archivePath);
        System.out.println("Размер архива: " + data.length + " байт");

        if (data.length < 4 + 64) {
            throw new IOException("Архив слишком мал");
        }

        if (!new String(Arrays.copyOf(data, 4)).equals("GOST")) {
            throw new IOException("Неверный формат архива");
        }

        byte[] storedMac = Arrays.copyOfRange(data, 4, 4 + 64);
        byte[] encrypted = Arrays.copyOfRange(data, 4 + 64, data.length);

        System.out.println("Размер зашифрованных данных: " + encrypted.length + " байт");

        byte[] keyMaterial = streebog512(password.getBytes(StandardCharsets.UTF_8));
        byte[] key1 = Arrays.copyOfRange(keyMaterial, 0, 16);
        byte[] key2 = Arrays.copyOfRange(keyMaterial, 16, 32);
        GOST_Kuz_Expand_Key(key1, key2);

        byte[] computedMac = streebog512(encrypted);
        if (!Arrays.equals(storedMac, computedMac)) {
            throw new IOException("MAC не совпадает: архив повреждён или пароль неверен");
        }

        byte[] compressed = decrypt(encrypted);
        System.out.println("Размер после дешифрования: " + compressed.length + " байт");

        byte[] packed = decompress(compressed);
        System.out.println("Размер после распаковки: " + packed.length + " байт");

        Files.createDirectories(outputDir);
        unpackFiles(packed, outputDir);
        System.out.println("Архив успешно распакован в: " + outputDir);
    }

    // =============== MAIN ===============
    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("GOSTArchiver — архиватор с шифрованием по ГОСТ Р 34.12-2018");
            System.err.println("Использование:");
            System.err.println("  -e <файл/папка> [...] -p <пароль> -o <архив.gost>");
            System.err.println("  -d <архив.gost> -p <пароль> -o <папка_назначения>");
            System.err.println("");
            System.err.println("Примеры:");
            System.err.println("  Архивирование: GOSTArchiver.exe -e file1.txt folder1 -p \"mypassword\" -o archive.gost");
            System.err.println("  Распаковка:    GOSTArchiver.exe -d archive.gost -p \"mypassword\" -o output_folder");
            System.exit(1);
        }

        String mode = null;
        String password = null;
        Path outputPath = null;
        List<Path> inputs = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {
            if ("-e".equals(args[i])) {
                mode = "encrypt";
            } else if ("-d".equals(args[i])) {
                mode = "decrypt";
            } else if ("-p".equals(args[i])) {
                password = args[++i];
            } else if ("-o".equals(args[i])) {
                outputPath = Paths.get(args[++i]);
            } else if (!args[i].startsWith("-")) {
                inputs.add(Paths.get(args[i]));
            }
        }

        if (mode == null || password == null || outputPath == null || inputs.isEmpty()) {
            System.err.println("Недостаточно аргументов");
            System.err.println("Необходимо указать режим (-e или -d), пароль (-p) и выходной путь (-o)");
            System.exit(1);
        }

        try {
            if ("encrypt".equals(mode)) {
                archiveAndEncrypt(inputs, password, outputPath);
            } else if ("decrypt".equals(mode)) {
                if (inputs.size() != 1) {
                    System.err.println("Для распаковки укажите один архив");
                    System.exit(1);
                }
                decryptAndExtract(inputs.get(0), password, outputPath);
            }
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
