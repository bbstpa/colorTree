package com.ysc.test;
/**
 * colorTree - 树状目录结构可视化工具
 * Copyright (c) 2025 bbstpa(ysc)
 * 
 * 本代码基于 MIT 协议授权：
 * https://opensource.org/licenses/MIT
 * 
 * 包含第三方组件：
 * - ICU4J (Unicode License)
 * - Jansi (Apache 2.0)
 */


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fusesource.jansi.AnsiConsole;
import org.fusesource.jansi.Ansi;

import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.lang.UProperty;



public class Main {
	private static boolean isKB = false;
	private static boolean onlyDir = false;
	private static boolean noColor = false;
	private static int displayLayer = -1;
	private static Map<Integer,String>spaceUnits=new HashMap<>(){{
		put(0, "");
		put(1, "K");
		put(2, "M");
		put(3, "G");
		put(4, "T");
		put(5, "P");
		put(6, "E");
		put(7, "Z");
		put(8, "Y");
		put(9, "B");
		put(10, "N");
		put(11, "D");
	}};

// 	Color0(0,100,200,255), Color1(1,0,0,205), 
// 	Color2(2,160,32,240),	Color3(3,255,0,0), 
// 	Color4(4,255,165,0), Color5(5,255,255,0), 
// 	Color6(6,0,128,0), Color7(7,144,238,144);

	private static List<int[]>colorSchemeRaw = new ArrayList<>(){{
		// add(new int[]{100,200,255});
		add(new int[]{0,0,205});
		add(new int[]{160,32,240});
		add(new int[]{255,0,0});
		add(new int[]{255,165,0});
		add(new int[]{255,255,0});
		add(new int[]{0,128,0});
		add(new int[]{144,238,144});
	}};
	

	private static List<int[]> colorSchemeBloodline = new ArrayList<>(){{
		add(new int[]{90,0,0});
		add(new int[]{139,0,0});
		add(new int[]{255,95,31});
		add(new int[]{255,255,0});
		add(new int[]{0,255,255});
		add(new int[]{255,0,255});
		add(new int[]{102,51,153});
	}};

	private static List<int[]> colorSchemeGothic = new ArrayList<>(){{
		add(new int[]{25,25,25});
		add(new int[]{48,25,52});
		add(new int[]{120,30,30});
		add(new int[]{0,100,0});
		add(new int[]{245,245,220});
		add(new int[]{8,232,222});
		add(new int[]{12,12,12});
	}};

	private static List<int[]> colorSchemeCyber = new ArrayList<>(){{
		add(new int[]{255,0,127});
		add(new int[]{255,100,0});
		add(new int[]{57,255,20});
		add(new int[]{0,255,255});
		add(new int[]{1477,112,219});
		add(new int[]{75,0,130});
		add(new int[]{192,192,192});
	}};

	private static List<int[]> colorSchemeNature = new ArrayList<>(){{
		add(new int[]{255,191,0});
		add(new int[]{34,139,34});
		add(new int[]{0,168,107});
		add(new int[]{31,81,255});
		add(new int[]{147,112,219});
		add(new int[]{255,255,153});
		add(new int[]{169,169,169});
	}};

	private static List<int[]> colorSchemeMachinery = new ArrayList<>(){{
		add(new int[]{74,74,74});
		add(new int[]{184,115,51});
		add(new int[]{65,105,225});
		add(new int[]{255,215,0});
		add(new int[]{40,40,40});
		add(new int[]{0,191,255});
		add(new int[]{192,192,192});
	}};

	private static List<int[]> colorSchemeFresh = new ArrayList<>(){{
		add(new int[]{255,182,193});
		add(new int[]{152,251,152});
		add(new int[]{135,206,235});
		add(new int[]{216,191,216});
		add(new int[]{255,218,185});
		add(new int[]{240,248,255});
		add(new int[]{245,245,245});
	}};

	private static List<int[]> colorSchemeRetro = new ArrayList<>(){{
		add(new int[]{75,0,130});
		add(new int[]{255,165,0});
		add(new int[]{0,255,0});
		add(new int[]{0,128,128});
		add(new int[]{255,0,127});
		add(new int[]{25,25,112});
		add(new int[]{255,223,0});
	}};

	private static List<int[]> colorSchemeWaStyle = new ArrayList<>(){{
		add(new int[]{227,66,52});
		add(new int[]{0,103,165});
		add(new int[]{255,195,0});
		add(new int[]{120,170,80});
		add(new int[]{146,52,141});
		add(new int[]{245,245,245});
		add(new int[]{46,42,35});
	}};
	// private static List<int[]> colorScheme = new ArrayList<>(){{
	// 	add(new int[]{});
	// 	add(new int[]{});
	// 	add(new int[]{});
	// 	add(new int[]{});
	// 	add(new int[]{});
	// 	add(new int[]{});
	// 	add(new int[]{});
	// }};

	static Map<String,List<int[]>> colorSchemeMap = new HashMap<>(){{
		put("raw",colorSchemeRaw);
		put("bloodline",colorSchemeBloodline);
		put("gothic",colorSchemeGothic);
		put("cyber",colorSchemeCyber);
		put("nature",colorSchemeNature);
		put("machinery",colorSchemeMachinery);
		put("fresh",colorSchemeFresh);
		put("retro",colorSchemeRetro);
		put("wa_style",colorSchemeWaStyle);
	}};
	static String[] colorSchemeName = {
		"raw","bloodline","gothic","cyber","nature","machinery","fresh","retro","wa_style"
	};
	static List<int[]> nowColorScheme = colorSchemeRaw;
	static int colorOffset = 0;
	static int alignSpace = 80;
	static boolean forceKB = false;
	static String outputFileName = "";
	static Writer writer;
	public static void main(String[] args) throws IOException {
		AnsiConsole.systemInstall();
		String path = ".";
		String arg;
        int pathIndex = -1;
		int layerIndex = -1;
		int colorStyleIndex = -1;
		int colorStyle = 0;
		int colorOffsetIndex = -1;
		int alignSpaceIndex = -1;
		int redirectIndex = -1;
		PrintStream utf8Out = new PrintStream(
            new FileOutputStream(FileDescriptor.out),
            true,
            StandardCharsets.UTF_8.name()
        );
        System.setOut(utf8Out);
        System.setErr(utf8Out);
        for (int i = 0; i < args.length; i++) {
			arg = args[i];
            if(arg.equals("-p") || arg.equals("--Path")) {
                pathIndex = i + 1;
            } else if(arg.equals("-od") || arg.equals("--OnlyDir")) {
				onlyDir = true;
			} else if(arg.equals("-l") || arg.equals("--Layer")) {
                layerIndex = i + 1;
            } else if(arg.equals("-s") || arg.equals("--Style")) {
                colorStyleIndex = i + 1;
            } else if(arg.equals("-of") || arg.equals("--Offset")) {
                colorOffsetIndex = i + 1;
            } else if(arg.equals("-a") || arg.equals("--Align")) {
                alignSpaceIndex = i + 1;
            } else if(arg.equals("-k") || arg.equals("--KB")) {
                isKB = true;
        	} else if(arg.equals("-fk") || arg.equals("--ForceKB")) {
                forceKB = true;
        	} else if(arg.equals("-r") || arg.equals("--Redirect")) {
                noColor = true;
				redirectIndex = i + 1;
            }
		}
		
        if(pathIndex != -1 && pathIndex < args.length) {
            path = args[pathIndex];
			path = replaceEnvCmdVars(path);
			path = replaceEnvPwshVars(path);
			// System.out.println("路径被解析为:" + path);
        }
		if(layerIndex != -1  && layerIndex < args.length) {
			try {
				displayLayer = Integer.parseInt(args[layerIndex]);	
			} catch (Exception e) {
				throw new IllegalArgumentException(errorColorString(String.format("参数layer的值%s不能正确的被识别为数值" ,args[layerIndex])), e);
			}
        }
		if(colorStyleIndex != -1  && colorStyleIndex < args.length) {
			try {
				colorStyle = Integer.parseInt(args[colorStyleIndex]);
				if(colorStyle >= 0 && colorStyle < colorSchemeMap.size()) {
					nowColorScheme = colorSchemeMap.get(colorSchemeName[colorStyle]);
				}
			} catch (Exception e) {
				throw new IllegalArgumentException(errorColorString(String.format("参数style的值%s不能正确地被识别为数值" ,args[colorStyleIndex])), e);
			}
        }
		if(colorOffsetIndex != -1  && colorOffsetIndex < args.length) {
			try {
				colorOffset = Integer.parseInt(args[colorOffsetIndex]);
				if (colorOffset < 0) {
					throw new IllegalArgumentException(errorColorString(String.format("%s不能为负数" ,args[colorOffsetIndex])));
				}
			} catch (Exception e) {
				throw new IllegalArgumentException(errorColorString(String.format("参数offset的值%s不能正确地被识别为数值" ,args[colorOffsetIndex])), e);
			}
        }
		if(alignSpaceIndex != -1  && alignSpaceIndex < args.length) {
			try {
				alignSpace = Integer.parseInt(args[alignSpaceIndex]);
				if (alignSpace < 0) {
					throw new IllegalArgumentException(errorColorString(String.format("%s不能为负数" ,args[alignSpaceIndex])));
				}
			} catch (Exception e) {
				throw new IllegalArgumentException(errorColorString(String.format("参数offset的值%s不能正确地被识别为数值" ,args[alignSpaceIndex])), e);
			}
        }
		if(redirectIndex != -1) {
			if(redirectIndex < args.length) {
				try {
					writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[redirectIndex]), StandardCharsets.UTF_8));
				} catch (Exception e) {
					
				}
			} else {
				throw new IllegalArgumentException(Ansi.ansi().fgRgb(255,0,0).a("重定向必须指向合适的文件名").reset().toString());
			}
        }

		for (int i = 0; i < args.length; i++) {
			arg = args[i];
			if(arg.equals("-v") || arg.equals("--Version")) {
				if(!noColor) {
					System.out.printf("%s version 1.0.2 | Copyright © 2025 %s %s\n\n",
					Ansi.ansi().fgGreen().a("colorTree").reset().toString(),
					Ansi.ansi().fgYellow().a("bbstpa(ysc)").reset().toString(),
					Ansi.ansi().fgBlue().a("ysctxdy@outlook.com").reset().toString());
					System.out.println(	"本软件基于 MIT 许可证发布：\n- 可自由使用、分发，但必须保留本声明\n- 作者不对软件质量作担保\n");
					System.out.println("第三方组件:\n" + //
										"- ICU4J 77.1 (Unicode License)\n" + //
										"- Jansi 2.4.0 (Apache 2.0)\n");
				} else {
					try {
						writer.write(String.format("%s version 1.0.2 | Copyright © 2025 %s %s\n\n",
					"colorTree","bbstpa(ysc)","ysctxdy@outlook.com"));
						writer.write("本软件基于 MIT 许可证发布：\n- 可自由使用、分发，但必须保留本声明\n- 作者不对软件质量作担保\n\n");
						writer.write("第三方组件:\n" + //
								"- ICU4J 77.1 (Unicode License)\n" + //
								"- Jansi 2.4.0 (Apache 2.0)\n\n");	
						writer.flush();
						writer.close();
					} catch (IOException e) {
						// TODO: handle exception
						e.printStackTrace();
					}
					
				}
				return ;
			} else if(arg.equals("-h") || arg.equals("--Help")) {
				printHelpInfo();
				return;
			}
		}
        // System.out.println(path);

		File currentDir = new File(path);
		// System.out.println(currentDir);
		if (currentDir.exists()) {
			if(currentDir.isDirectory()) {
				listDir(currentDir.getCanonicalFile());		
			} else {
				int localAlign = (alignSpace - getDisplayWidth(currentDir.getName()) - 1);
				try {
					if(localAlign > 0) {
						if(!noColor)
						System.out.println(colorString(String.format("%s %" + (localAlign) + "s", currentDir.getName(),formatSpaceString(currentDir.length())), 0));
						else
						writer.write(String.format("%s %" + (localAlign) + "s", currentDir.getName(),formatSpaceString(currentDir.length())));
					} else {
						if(!noColor)
						System.out.println(colorString(String.format("%s %s", currentDir.getName(),formatSpaceString(currentDir.length())), 0));
						else
						writer.write(String.format("%s %s", currentDir.getName(),formatSpaceString(currentDir.length())));
					}
					writer.flush();
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return ;
			}
		} else {
			try {
				throw new IllegalArgumentException(errorColorString(String.format("路径【%s】不存在\n",currentDir.getCanonicalPath())));	
			} catch (Exception e) {
				throw new IllegalArgumentException(errorColorString("--Path参数异常") , e);
			} 
		}
		
		
		
		// System.out.println(Arrays.toString(currentDir.list()));
		AnsiConsole.systemUninstall();
	}

	private static String replaceEnvCmdVars(String path) {
		Pattern pattern = Pattern.compile("%(\\w+)%");
        Matcher matcher = pattern.matcher(path);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String varName = matcher.group(1);
            String varValue = System.getenv(varName);
            if (varValue == null) {
                // 环境变量不存在，保留原样
                matcher.appendReplacement(sb, Matcher.quoteReplacement(matcher.group()));
            } else {
                matcher.appendReplacement(sb, Matcher.quoteReplacement(varValue));
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
	}

	private static String replaceEnvPwshVars(String path) {
		Pattern pattern = Pattern.compile("\\$env:(\\w+)");
        Matcher matcher = pattern.matcher(path);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String varName = matcher.group(1);
            String varValue = System.getenv(varName);
            if (varValue == null) {
                // 环境变量不存在，保留原样
                matcher.appendReplacement(sb, Matcher.quoteReplacement(matcher.group()));
            } else {
                matcher.appendReplacement(sb, Matcher.quoteReplacement(varValue));
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
	}

	static void printHelpInfo() {
		if(!noColor) {
			System.out.println("以树状显示文件夹结构及大小\n");
			System.out.println("用法：colorTree [选项] ");
			System.out.println("可选的选项有：\n");
			System.out.printf("\t%s,%s <路径>\n","-p","--Path");
			System.out.println("\t\t\t\t用于展示结构的目录路径，默认为当前路径\n\t\t\t\t支持形如 %变量% 和 $env:变量 的环境变量作为输入");
			System.out.printf("\t%s,%s\n","-od","--OnlyDir");
			System.out.println("\t\t\t\t不输出文件，只输出文件夹信息");
			System.out.printf("\t%s,%s <层数>\n","-l","--Layer");
			System.out.println("\t\t\t\t最大显示层级深度,负数会被忽略");
			System.out.printf("\t%s,%s <配色方案索引>\n","-s","--Style");
			System.out.println("\t\t\t\t使用内置配色方案的索引，\n\t\t\t\t有效值为0-8，默认为0");
			System.out.printf("\t%s,%s <配色方案起始颜色索引>\n","-of","--Offset");
			System.out.println("\t\t\t\t配色方案的起始位置，\n\t\t\t\t每个配色方案都是7个颜色循环，所以大于6的值会被取余");
			System.out.printf("\t%s,%s <对齐位置>\n","-a","--Align");
			System.out.println("\t\t\t\t文件的大小信息在第几列对齐，默认值为80");
			System.out.printf("\t%s,%s\n","-k","--KB");
			System.out.println("\t\t\t\t文件大小使用1000进制(KB)，默认为1024进制(KiB)");
			System.out.printf("\t%s,%s\n","-fk","--ForceKB");
			System.out.println("\t\t\t\t即使是1024进制也强制使用形如KB的形式作为单位");
			System.out.printf("\t%s,%s <文件路径>\n","-r","--Redirect");
			System.out.println("\t\t\t\t不使用任何颜色方案，重定向输出到指定文件");
			System.out.printf("\t%s,%s\n","-v","--Version");
			System.out.println("\t\t\t\t输出版本信息和版权声明");
			System.out.printf("\t%s,%s\n","-h","--Help");
			System.out.println("\t\t\t\t输出此帮助信息\n");
		} else {
			try {
				writer.write("以树状显示文件夹结构及大小\n\n");
				writer.write("用法：colorTree [选项] \n");
				writer.write("可选的选项有：\n\n");
				writer.write(String.format("\t%s,%s <路径>\n","-p","--Path"));
				writer.write("\t\t\t\t用于展示结构的目录路径，默认为当前路径\n\t\t\t\t支持形如 %变量% 和 $env:变量 的环境变量作为输入\n");
				writer.write(String.format("\t%s,%s\n","-od","--OnlyDir"));
				writer.write("\t\t\t\t不输出文件，只输出文件夹信息\n");
				writer.write(String.format("\t%s,%s <层数>\n","-l","--Layer"));
				writer.write("\t\t\t\t最大显示层级深度,负数会被忽略\n");
				writer.write(String.format("\t%s,%s <配色方案索引>\n","-s","--Style"));
				writer.write("\t\t\t\t使用内置配色方案的索引，\n\t\t\t\t有效值为0-8，默认为0\n");
				writer.write(String.format("\t%s,%s <配色方案起始颜色索引>\n","-of","--Offset"));
				writer.write("\t\t\t\t配色方案的起始位置，\n\t\t\t\t每个配色方案都是7个颜色循环，所以大于6的值会被取余\n");
				writer.write(String.format("\t%s,%s <对齐位置>\n","-a","--Align"));
				writer.write("\t\t\t\t文件的大小信息在第几列对齐，默认值为80\n");
				writer.write(String.format("\t%s,%s\n","-k","--KB"));
				writer.write("\t\t\t\t文件大小使用1000进制(KB)，默认为1024进制(KiB)\n");
				writer.write(String.format("\t%s,%s\n","-fk","--ForceKB"));
				writer.write("\t\t\t\t即使是1024进制也强制使用形如KB的形式作为单位\n");
				writer.write(String.format("\t%s,%s <文件路径>\n","-r","--Redirect"));
				writer.write("\t\t\t\t不使用任何颜色方案，重定向输出到指定文件");
				writer.write(String.format("\t%s,%s\n","-v","--Version"));
				writer.write("\t\t\t\t输出版本信息和版权声明\n");
				writer.write(String.format("\t%s,%s\n","-h","--Help"));
				writer.write("\t\t\t\t输出此帮助信息\n\n");
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	static void printIndent(int layer) {
		try {
			if (!noColor) {
				for (int i = 0; i < layer; i++) {
					System.out.print("  ");
				}	
			} else {
				for (int i = 0; i < layer; i++) {
					writer.write("  ");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

	static void listDir(File dir) {
		int firstColorOrder = 1;
		Deque<DirInfo> dirStack = new ArrayDeque<>();	// 模拟递归调用的栈
		Deque<FileInfo> resultList = new LinkedList<>();	// 保存最终输出信息的队列

		dirStack.push(new DirInfo(0, dir));
		// 记录所有文件文件夹名称，并计算文件大小还有不包括子文件夹的文件夹大小
		while (!dirStack.isEmpty()) {
			DirInfo dirInfo = dirStack.pop();
			File subDir = dirInfo.getDir();
			int dirLayer = dirInfo.getLayer();
			resultList.addLast(new FileInfo("D",dirLayer , subDir.getName() + "/"));
			// int layer = dirInfo.layer; // System.out.println(dirInfo);
			// printIndent(layer);
			// System.out.println(colorString(subDir.getName() + "/", (layer + firstColorOrder) % 8));
			FileInfo nowDir = resultList.getLast();
			List<File> subDirList = new LinkedList<>();
			if(checkDirCanRead(subDir)  && subDir.listFiles() != null) {
				for (File f : subDir.listFiles()) {
						if (f.isFile()) {
							// if(f.canRead()) {
								resultList.addLast(new FileInfo("F",dirLayer+1, f.getName(), f.length()));
							// } else {
							// 	resultList.addLast(new FileInfo("F",dirLayer+1, f.getName(), f.length()));
							// }
							nowDir.addSize(f.length());
							// printIndent(layer + 1);
							// System.out.println(colorString(f.getName() + "\t" + f.length(), (layer + 1 + firstColorOrder) % 8));
						} else if (f.isDirectory()) {
							subDirList.add(f);
		//					dirStack.push(new DirInfo(layer + 1, f));
						}
				}
				if (!subDirList.isEmpty()) {
					subDirList = subDirList.reversed();
					for (File f : subDirList) {
						dirStack.push(new DirInfo(dirLayer + 1, f));
					}	
				}
			}
		}
		// 从后往前根据层级将子文件夹的大小附加到父文件夹
		ListIterator<FileInfo> outIter = 
		((LinkedList<FileInfo>) resultList).listIterator(resultList.size());
		while(outIter.hasPrevious()) {
			FileInfo subDirInfo = outIter.previous();
			if(subDirInfo.getType().equals("D") && outIter.hasPrevious()) {
				ListIterator<FileInfo> inIter = ((LinkedList<FileInfo>) resultList).listIterator(outIter.nextIndex());
				while (inIter.hasPrevious()) {
					FileInfo DirInfo = inIter.previous();
					if(DirInfo.getLayer() == (subDirInfo.getLayer() - 1)) {
						DirInfo.addSize(subDirInfo.getSize());
						break;
					}
				}
			}
		}
		try {
			if (!noColor) {
				for (FileInfo fileInfo : resultList) {
					if (!onlyDir || fileInfo.getType().equals("D")) {
						if (displayLayer == -1 || fileInfo.getLayer() <= displayLayer) {
							System.out.println(myFormatString(fileInfo));	
						}
					}
				}	
			} else {
				for (FileInfo fileInfo : resultList) {
					if (!onlyDir || fileInfo.getType().equals("D")) {
						if (displayLayer == -1 || fileInfo.getLayer() <= displayLayer) {
							writer.write(myFormatString(fileInfo) + "\n");	
							writer.flush();
						}
					}
				}
				writer.close();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		

		// for (FileInfo fileInfo : resultList) {
		// 	System.out.println(myFormatString(fileInfo));
		// }

	}

	static int getDisplayWidth(String text) {
        return text.codePoints()
                .map(c -> {
					int eaw = UCharacter.getIntPropertyValue(c, UProperty.EAST_ASIAN_WIDTH);
					// 处理全角(F)、宽(W)和模糊(A)字符为双宽度
					if (eaw == UCharacter.EastAsianWidth.FULLWIDTH || 
						eaw == UCharacter.EastAsianWidth.WIDE || 
						eaw == UCharacter.EastAsianWidth.AMBIGUOUS) {
						return 2;
					} else {
						return 1;
					}
				})
                .sum();
    }

	static boolean checkDirCanRead(File dir) {
		return (dir.exists() && dir.isDirectory() && dir.canRead());
	}

	static String myFormatString(FileInfo f) {
		int layer = f.getLayer();
		// System.out.println(f.getName()+ "的显示宽度为:\t" + getDisplayWidth(f.getName()));
		int localAlign = (alignSpace - layer * 2 - getDisplayWidth(f.getName()) - 1);
		if(localAlign > 0)
		return colorString(String.format("%s%s %" + (localAlign) + "s", indentString(layer),f.getName(),formatSpaceString(f.getSize())), layer);
		return colorString(String.format("%s%s %s", indentString(layer),f.getName(),formatSpaceString(f.getSize())), layer);
	}

	static String formatSpaceString(Long size) {
		SpaceInfo spaceInfo = new SpaceInfo(size,isKB);
		return 	String.format("%.2f%s%s", spaceInfo.getResultSize() ,
				spaceUnits.get(spaceInfo.getResultTypeIndex()) ,
				(forceKB?"B":(isKB?"B":(spaceInfo.getResultTypeIndex()>0?"iB":"B"))));
	}

	static String indentString(int layer) {
		StringBuilder sb = new StringBuilder("");
		for (int i = 0; i < layer; i++) {
			sb.append("  ");
		}
		return sb.toString();
	}

	static String errorColorString(String str) {
		if (!noColor)
		return Ansi.ansi().fgRgb(255,0,0).a(str).reset().toString();
		return str;
	}

	static String colorString(String str, int layer) {
		// return Color.fromOrder(colorOrder).colorString(str);
		int limit = nowColorScheme.size();
		// System.out.println("配色方案大小\t\t\t" + limit);
		if(!noColor) {
			int colorNum = (layer + colorOffset) % limit;
			if ( colorNum >= 0) {
				int[] rgb = nowColorScheme.get(colorNum);
				return Ansi.ansi().fgRgb(rgb[0],rgb[1],rgb[2]).a(str).reset().toString();
			}
		}
		return str;
	}

	static class SpaceInfo {
		private Long rawSize;
		private Double resultSize;
		private int resultTypeIndex;
		public SpaceInfo(Long size,boolean isKB) {
			rawSize = size;
			resultSize = size.doubleValue();
			resultTypeIndex = 0;
			if (isKB) {
				while (resultSize >= 1000 && resultTypeIndex < spaceUnits.size()) {
					resultSize/=1000;
					resultTypeIndex++;
				}
			} else {
				while (resultSize >= 1024 && resultTypeIndex < spaceUnits.size()) {
					resultSize/=1024;
					resultTypeIndex++;
				}
			}
		}
		public Double getResultSize() {
			return resultSize;
		}
		public Long getRawSize() {
			return rawSize;
		}
		public int getResultTypeIndex() {
			return resultTypeIndex;
		}
	}

	static class DirInfo {
		private int layer;
		private File dir;
		public DirInfo(int layer, File dir) {
			this.layer = layer;
			this.dir = dir;
		}

		public int getLayer() {
			return layer;
		}
		public File getDir() {
			return dir;
		}

		@Override
		public String toString() { 
			return "layer:" + layer + "\tdir:" + dir;
		}
	}

	static class FileInfo {
		private int layer; 		// 层级 影响缩进和大小往回添加的位置
		private String name;	// 文件名
		private String type;	// 文件类型 用于识别是否为文件夹 F 文件 D 目录
		private Long size;		// 在输出时再转格式以保留精度
		private String special;	// 保存声明为特殊文件的标准

		FileInfo(String type,int layer,String name) {
			this.type = type;
			this.layer = layer;
			this.name = name;
			this.size = 0l;
			this.special = null;
		}
		FileInfo(String type,int layer,String name,long size) {
			this(type,layer, name);
			this.size = size;
		}
		public int getLayer() {
			return layer;
		}
		public String getName() {
			return name;
		}
		public long getSize() {
			return size;
		}
		public String getSpecial() {
			return special;
		}
		public String getType() {
			return type;
		}

		public void addSize(Long size) {
			this.size += size;
		}

		@Override
		public String toString() {
			return "文件名: " + getName() + "大小: " + getSize();
		}
	}

}

// enum Color {
// 	Color0(0,100,200,255), Color1(1,0,0,205), 
// 	Color2(2,160,32,240),	Color3(3,255,0,0), 
// 	Color4(4,255,165,0), Color5(5,255,255,0), 
// 	Color6(6,0,128,0), Color7(7,144,238,144);

// 	private final int colorOrder;
// 	private final Ansi colorAnsi;

// 	private Color(int order, int r,int g,int b) {
// 		colorAnsi = Ansi.ansi().fgRgb(r,g,b);
// 		colorOrder = order;
// 	}

// 	public static Color fromOrder(int order) {
// 		for (Color c : values()) {
// 			if (c.colorOrder == order) {
// 				return c;
// 			}
// 		}
// 		throw new IllegalArgumentException("此序号没有对应的颜色: " + order);
// 	}

// 	public String colorString(String str) {
// 		return (colorAnsi.a(str).reset()).toString();
// 	}
// 	// 同个Ansi实例是状态累计的，在String时会导致之前的内容一直被错误的附加
// }
