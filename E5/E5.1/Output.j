.class public Output 
.super java/lang/Object

.method public <init>()V
 aload_0
 invokenonvirtual java/lang/Object/<init>()V
 return
.end method

.method public static print(I)V
 .limit stack 2
 getstatic java/lang/System/out Ljava/io/PrintStream;
 iload_0 
 invokestatic java/lang/Integer/toString(I)Ljava/lang/String;
 invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
 return
.end method

.method public static read()I
 .limit stack 3
 new java/util/Scanner
 dup
 getstatic java/lang/System/in Ljava/io/InputStream;
 invokespecial java/util/Scanner/<init>(Ljava/io/InputStream;)V
 invokevirtual java/util/Scanner/next()Ljava/lang/String;
 invokestatic java/lang/Integer.parseInt(Ljava/lang/String;)I
 ireturn
.end method

.method public static run()V
 .limit stack 1024
 .limit locals 256
 invokestatic Output/read()I
 istore 0
 goto L1
L1:
 iload 0
 ldc 1
 iadd 
 invokestatic Output/print(I)V
 goto L2
L2:
 ldc 10
 istore 1
 ldc 20
 istore 2
 ldc 30
 istore 3
 goto L3
L3:
 iload 1
 iload 2
 iload 3
 imul 
 iadd 
 invokestatic Output/print(I)V
 goto L4
L4:
 iload 0
 ldc 3
 if_icmpgt L5
 goto L6
L5:
 iload 0
 invokestatic Output/print(I)V
 goto L8
L8:
 goto L7
L6:
 ldc 1
 invokestatic Output/print(I)V
 goto L9
L9:
L7:
 ldc 0
 istore 4
L10:
 iload 4
 ldc 7
 if_icmpgt L11
 iload 4
 invokestatic Output/print(I)V
 goto L12
L12:
 iload 4
 ldc 1
 iadd 
 istore 4
 goto L13
L13:
 goto L10
L11:
 ldc 10
 istore 5
L14:
 iload 5
 ldc 0
 if_icmpgt L15
 iload 5
 invokestatic Output/print(I)V
 goto L16
L16:
 iload 5
 ldc 1
 isub 
 istore 5
 goto L17
L17:
 goto L14
L15:
 iload 0
 ldc 5
 if_icmpgt L18
 goto L19
L18:
 ldc 10
 istore 1
L21:
 iload 1
 ldc 0
 if_icmplt L22
 iload 1
 invokestatic Output/print(I)V
 goto L23
L23:
 iload 1
 ldc 1
 isub 
 istore 1
 goto L24
L24:
 goto L21
L22:
L19:
 goto L20
L20:
 ldc 5
 istore 2
 goto L25
L25:
L26:
 iload 2
 ldc 0
 if_icmplt L27
 iload 2
 ldc 1
 isub 
 istore 2
 goto L28
L28:
 goto L26
L27:
 iload 2
 invokestatic Output/print(I)V
 goto L29
L29:
 ldc 0
 istore 3
 goto L30
L30:
L31:
 iload 3
 ldc 5
 if_icmpgt L32
 iload 3
 invokestatic Output/print(I)V
 goto L33
L33:
 iload 3
 ldc 1
 iadd 
 istore 3
 goto L34
L34:
 goto L31
L32:
 goto L0
L0:
 return
.end method

.method public static main([Ljava/lang/String;)V
 invokestatic Output/run()V
 return
.end method

