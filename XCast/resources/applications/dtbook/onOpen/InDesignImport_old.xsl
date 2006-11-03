<?xml version="1.0" encoding="UTF-8"?>
<!-- 
Sida	A1	A2	A3	A4	A5	A6	A7	A8	A9	AA	AB
1		X
2			X	X
3					X
4					X
5					X
6						X		
7						X	X	X
8						X	X	X
9									X
10									X	X
11									X	X
12										X
13											X	X
14											X	X
15											X	X

Sida 1 är enkel
Sida 2 återges efter varandra i ordningen uppifrån och ner från vänster till höger (båda får sidnummer)
Sida 3-5 är enkel
Sida 6-8 återger A5 med A6 och A7 infogade före sista stycket på den sida där de börjar
Sida 9-12 återges efter varandra (båda får sidnummer)
Sida 13-15 återges efter varandra i ordningen uppifrån och ner från vänster till höger (båda får sidnummer)

Justeringar behövs för att ta hand om bilder och tabeller.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:include href="lib/recursive-copy.xsl"/>
	<xsl:output indent="no" method="xml" encoding="UTF-8"/>
	
	<xsl:template match="/">
		<dtbook>
			<head>
				<meta name="dc:Title" content=""/>
				<meta name="dc:Creator" content=""/>
				<meta name="dc:Publisher" content=""/>
				<meta name="dc:Date" content=""/>
				<meta name="dc:Language" content=""/>
				<meta name="dtb:uid" content=""/>
			</head>
			<book>
				<bodymatter>
					<xsl:apply-templates/>
				</bodymatter>
			</book>
		</dtbook>
	</xsl:template>
	
	<xsl:param name="debug" select="'false'"/>
	
	<xsl:variable name="min"><xsl:apply-templates select="(//page)[1]" mode="calcMin">
		<xsl:with-param name="cMin" select="number((//page)[1]/@no)"/>
	</xsl:apply-templates></xsl:variable>
	
	<xsl:variable name="max"><xsl:apply-templates select="(//page)[1]" mode="calcMax">
		<xsl:with-param name="cMax" select="number((//page)[1]/@no)"/>
	</xsl:apply-templates></xsl:variable>
	
	<xsl:variable name="overlap"><xsl:apply-templates select="(//Artikel)[1]" mode="countArticleOverlap"/></xsl:variable>
	
	<xsl:variable name="mp-article-count" select="count((//Artikel)[count(descendant::page/@no)>1])"/>
		
	<xsl:template match="page" mode="calcMin">
		<xsl:param name="cMin" select="0"/>
		<xsl:choose>
			<xsl:when test="number(@no)&lt;number($cMin)">
				<xsl:apply-templates select="following::page[1]" mode="calcMin">
					<xsl:with-param name="cMin" select="number(@no)"/>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="following::page[1]" mode="calcMin">
					<xsl:with-param name="cMin" select="number($cMin)"/>
				</xsl:apply-templates>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:if test="not(following::page)">
			<xsl:value-of select="$cMin"/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="page" mode="calcMax">
		<xsl:param name="cMax" select="0"/>
		<xsl:choose>
			<xsl:when test="number(@no)&gt;number($cMax)">
				<xsl:apply-templates select="following::page[1]" mode="calcMax">
					<xsl:with-param name="cMax" select="number(@no)"/>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="following::page[1]" mode="calcMax">
					<xsl:with-param name="cMax" select="number($cMax)"/>
				</xsl:apply-templates>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:if test="not(following::page)">
			<xsl:value-of select="$cMax"/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="Artikel" mode="countArticleOverlap">
		<xsl:param name="overlap" select="0"/>
		<xsl:variable name="pagenum" select="@no"/>
		<xsl:variable name="count">
			<xsl:choose>
				<xsl:when test="count(//Artikel[@id!=current()/@id and descendant::page/@no=current()/descendant::page/@no])&gt;1">1</xsl:when>
				<xsl:otherwise>0</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:apply-templates select="following::Artikel[1]" mode="countArticleOverlap">
			<xsl:with-param name="overlap" select="number($overlap) + number($count)"/>
		</xsl:apply-templates>
		<xsl:if test="not(following::Artikel)">
			<xsl:value-of select="number($overlap) + number($count)"/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="page" mode="countMultiPageOverlap">
		<xsl:param name="overlap" select="0"/>
		<xsl:variable name="pagenum" select="@no"/>
		<xsl:variable name="count">
			<xsl:choose>
				<xsl:when test="count(//Artikel[descendant::page/@no=$pagenum]/descendant::page)&gt;1">1</xsl:when>
				<xsl:otherwise>0</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:apply-templates select="following::page[1]" mode="countMultiPageOverlap">
			<xsl:with-param name="overlap" select="number($overlap) + number($count)"/>
		</xsl:apply-templates>
		<xsl:if test="not(following::page)">
			<xsl:value-of select="number($overlap) + number($count)"/>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="Root">
		<xsl:if test="$debug!='false'">
			<debug>
				<v>Min: <xsl:value-of select="$min"/></v>
				<v>Max: <xsl:value-of select="$max"/></v>
				<v>Multipage article count: <xsl:value-of select="$mp-article-count"/></v>
				<v>overlaping article count: <xsl:value-of select="$overlap"/></v>
			</debug>
		</xsl:if>
				<xsl:for-each select="Artikel">
					<!-- Sortera först på sidnummer, sedan på y-koordinat, sedan på x-koordinat -->
					<xsl:sort select="concat(descendant::page[1]/@no, format-number(descendant::page[1]/@y1, 'XXXXXXX00000.0;00000.0XXXXXXX'), format-number(descendant::page[1]/@x1, 'XXXXXXX00000.0;00000.0XXXXXXX'))"/>
					<xsl:variable name="a-min" select="./descendant::page[1]/@no"/>
					<xsl:variable name="a-max" select="./descendant::page[last()]/@no"/>
					<!-- hur många andra artiklar finns det som rymmer den här artikeln? -->
					<xsl:variable name="sub" select="count(//Artikel[descendant::page/@no&lt;$a-min and descendant::page/@no>=$a-max or descendant::page/@no&lt;=$a-min and descendant::page/@no>$a-max])"/>
					<xsl:if test="$debug!='false'">
						<debug>
							<v>a-min: <xsl:value-of select="$a-min"/></v>
							<v>a-max: <xsl:value-of select="$a-max"/></v>
						</debug>
					</xsl:if>
					<!-- process here if this is not a part of another story... -->
					<xsl:if test="number($sub)=0">
						<xsl:apply-templates select="."/>
					</xsl:if>
				</xsl:for-each>
	</xsl:template>

	<xsl:template name="insertArticles">
				<xsl:variable name="current-page" select="preceding-sibling::*/descendant::page[1]/@no"/>
				<xsl:variable name="a-min" select="parent::Artikel/descendant::page[1]/@no"/>
				<xsl:variable name="a-max" select="parent::Artikel/descendant::page[last()]/@no"/>
				<!-- select all articles that begin on this page and end before this one do-->
				<xsl:for-each select="//Artikel[descendant::page[1]/@no=$current-page and descendant::page[last()]/@no&lt;=$a-max and descendant::page[last()]/@no - descendant::page[1]/@no&lt;$a-max - $a-min]">
					<xsl:sort select="concat(descendant::page[1]/@no, format-number(descendant::page[1]/@y1, 'XXXXXXX00000.0;00000.0XXXXXXX'), format-number(descendant::page[1]/@x1, 'XXXXXXX00000.0;00000.0XXXXXXX'))"/>
					<xsl:apply-templates select="." mode="moved"/>
				</xsl:for-each>
	</xsl:template>
	
	<xsl:template match="*[parent::Artikel]">
		<xsl:choose>
			<xsl:when test="parent::Artikel and descendant::page">
				<xsl:call-template name="insertArticles"/>
				<xsl:call-template name="copy"/>
			</xsl:when>
			<xsl:when test="parent::Artikel and not(following-sibling::*)">
				<xsl:call-template name="copy"/>
				<xsl:call-template name="insertArticles"/>
			</xsl:when>
			<xsl:otherwise><xsl:call-template name="copy"/></xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="page">
		<xsl:variable name="pagenum" select="@no"/>
		<pagenum id="p-{$pagenum}">
			<xsl:value-of select="$pagenum"/>
		</pagenum>
	</xsl:template>
	
	<xsl:template match="Artikel">
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="text()">
		<xsl:value-of select="translate(string(.), '&#x2028;&#x2029;', '')"/>
	</xsl:template>

	<xsl:template match="text()" mode="moved">
		<xsl:value-of select="translate(string(.), '&#x2028;&#x2029;', '')"/>
	</xsl:template>

	<xsl:template match="*|comment()|processing-instruction()" mode="moved">
		<xsl:call-template name="copyMoved"/>
	</xsl:template>

	<xsl:template name="copyMoved">
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates mode="moved"/>
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="page" mode="moved">
		<xsl:comment>Sidan: <xsl:value-of select="@no"/></xsl:comment>
	</xsl:template>
	
</xsl:stylesheet>
