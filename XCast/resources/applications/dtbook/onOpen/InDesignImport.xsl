<?xml version="1.0" encoding="UTF-8"?>
<!--
	InDesign Import
		Version
			2006-12-21

		Description
			Handles generic InDesign markup with the following restrictions:
				* The root node is called "Root"
				* Child elements of Root is called "Artikel"
				* Each Artikel contains at least one "page" element
				* A page element is empty and contains these attributes:
					x1, y1, no, name

		Known issues
			Table handling has limitations. Some row-/colspan problems can occur. Manual
				checking of complex tables is recommended.
			Several pagenum tags can share the same id. Duplicates should be removed
				using a separate postprocessing step (XCast/AInDesign-fix.xsl).

		Namespaces
			(x) ""

		Doctype assignment
			( ) DTBook
			(x) None

		Tests
			XMLSpy XSLT engine	( ) 2005	( ) 2006	( ) 2007
			MSXML				( ) 3.0		(x) 4.0
			Saxon				(x) 6.5.3	( ) 8.8

			(x) = pass
			(-) = fail
			( ) = not tested

		Author
			Joel Håkansson, TPB
-->
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
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:aid="http://ns.adobe.com/AdobeInDesign/4.0/" exclude-result-prefixes="aid">
	<xsl:include href="lib/recursive-copy.xsl"/>
	<xsl:output indent="no" method="xml" encoding="UTF-8"/>
	
	<xsl:template match="/">
		<!-- Run startup tests -->
		<xsl:if test="not(/Root)">
			<xsl:message terminate="yes">Error 73: Wrong root element</xsl:message>
		</xsl:if>
		<xsl:if test="/Root/*[not(self::Artikel)]">
			<xsl:message terminate="yes">Error 76: Unpredicted child of Root: <xsl:for-each select="/Root/*[not(self::Artikel)]">
"<xsl:value-of select="name()"/>"</xsl:for-each>
			</xsl:message>
		</xsl:if>
		<xsl:if test="//Artikel[not(page)]">
			<xsl:message terminate="yes">Error 81: The document contain articles without page information</xsl:message>
		</xsl:if>
		<xsl:if test="//Artikel/*[1][not(self::page)]">
			<xsl:message terminate="yes">Error 84: The document contain articles whose first child element is not a page element</xsl:message>
		</xsl:if>
		<xsl:if test="count(//page[@no and @x1 and @y1 and @name])!=count(//page)"><xsl:message terminate="yes">Error 86: Unexpected page tag: <xsl:for-each select="//page[not(@no and @x1 and @y1 and @name)]">
"<xsl:value-of select="name()"/><xsl:for-each select="@*"><xsl:value-of select="concat(' ', name(), '=', .)"/></xsl:for-each>"</xsl:for-each>
			</xsl:message>
		</xsl:if>
		
		<!-- test for duplicate pagenums inside the same article -->
		
		<!--<xsl:if test=""><xsl:message terminate="yes"></xsl:message></xsl:if>-->
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

	<xsl:template match="/Root">
		<!-- Select and process any stories that are not part of another story -->
		<!-- Sort order: pagenumber, y-coordinate, x-coordinate -->
		<xsl:for-each select="Artikel">
			<xsl:sort select="(descendant::page[1])/@no" data-type="number"/>
			<xsl:sort select="(descendant::page[1])/@y1" data-type="number"/>
			<xsl:sort select="(descendant::page[1])/@x1" data-type="number"/>
			<xsl:variable name="aMin" select="(descendant::page)[1]/@no"/>
			<xsl:variable name="aMax" select="(descendant::page)[last()]/@no"/>
			<xsl:variable name="sub" select="count(//Artikel[(descendant::page)[1]/@no&lt; $aMin and (descendant::page)[last()]/@no>=$aMax or (descendant::page)[1]/@no&lt;=$aMin and (descendant::page)[last()]/@no> $aMax])"/>
			<xsl:if test="number($sub)=0"><xsl:call-template name="copyArticle"/></xsl:if>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="copyArticle">
		<xsl:variable name="pageMin" select="number((descendant::page)[1]/@no)"/>
		<xsl:variable name="pageMax" select="number((descendant::page)[last()]/@no)"/>
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<!-- If multipaged article, insert any single page articles on this page -->
			<xsl:if test="$pageMax - $pageMin &gt; 0">
				<xsl:for-each select="//Artikel[(descendant::page)[1]/@no=(descendant::page)[last()]/@no and (descendant::page)[1]/@no=$pageMin]">
					<xsl:sort select="descendant::page[1]/@no" data-type="number"/>
					<xsl:sort select="descendant::page[1]/@y1" data-type="number"/>
					<xsl:sort select="descendant::page[1]/@x1" data-type="number"/>
					<xsl:copy>
						<xsl:copy-of select="@*"/>
						<xsl:apply-templates/>
					</xsl:copy>
				</xsl:for-each>
			</xsl:if>
			<xsl:apply-templates/>
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="Artikel">
		<xsl:message terminate="yes">Error 147: Document contains unsorted articles.</xsl:message>
	</xsl:template>

	<xsl:template name="insertArticles">
		<xsl:variable name="current-page" select="(preceding-sibling::*/descendant::page)[1]/@no"/>
		<xsl:variable name="a-min" select="(parent::Artikel/descendant::page)[1]/@no"/>
		<xsl:variable name="a-max" select="(parent::Artikel/descendant::page)[last()]/@no"/>

		<!-- Select all articles that begin on this page and end before this one do -->
		<xsl:for-each select="//Artikel[number((descendant::page)[1]/@no)=number($current-page) and number((descendant::page)[last()]/@no)&lt;=number($a-max)]">
		<!--  and number((descendant::page)[last()]/@no) - number((descendant::page)[1]/@no)&lt;number($a-max) - number($a-min) -->
			<xsl:sort select="descendant::page[1]/@no" data-type="number"/>
			<xsl:sort select="descendant::page[1]/@y1" data-type="number"/>
			<xsl:sort select="descendant::page[1]/@x1" data-type="number"/>
			<xsl:call-template name="copyArticle"/>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="insertPagenum">
		<pagenum id="page-{@name}"><xsl:value-of select="@name"/></pagenum>
	</xsl:template>
	
	<xsl:template match="page" priority="5">
		<xsl:call-template name="insertPagenum"/>
	</xsl:template>

<!--
	<xsl:template match="page" mode="moved">
		<xsl:comment>Sidan: <xsl:value-of select="@name"/></xsl:comment>
	</xsl:template>-->
	
	<xsl:template match="*[parent::Artikel[(descendant::page)[1]/@no!=(descendant::page)[last()]/@no]]">
		<xsl:choose>
		<!--
			<xsl:when test="self::page">
				<xsl:call-template name="insertPagenum"/>
				<xsl:call-template name="insertArticles"/>
			</xsl:when>-->
		<!--
			<xsl:when test="self::Figure"><img src="{@href_fmt}"/></xsl:when>
			<xsl:when test="parent::Artikel and descendant::page">
				<xsl:call-template name="insertArticles"/>
				<xsl:call-template name="copy"/>
			</xsl:when> -->
			<xsl:when test="parent::Artikel and not(following-sibling::*)">
				<xsl:call-template name="copy"/>
				<xsl:call-template name="insertArticles"/>
			</xsl:when>
			<xsl:otherwise><xsl:call-template name="copy"/></xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="*[@href_fmt and count(descendant::text())=0]" priority="10">
		<img src="{@href_fmt}"/>
	</xsl:template>

	<xsl:template match="Cell" mode="calc">
		<xsl:variable name="prod">
			<xsl:choose>
				<xsl:when test="count(preceding-sibling::Cell)&gt;0">
					<xsl:apply-templates select="preceding-sibling::Cell[1]" mode="calc"/>
				</xsl:when>
				<xsl:otherwise>-1</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:value-of select="number($prod) + @aid:ccols * @aid:crows"/>
	</xsl:template>
	
	<xsl:template match="Cell">
		<xsl:variable name="offset"><xsl:apply-templates select="." mode="calc"/></xsl:variable>
		<xsl:if test="number($offset) mod ../@aid:tcols = 0">
			<tr><xsl:call-template name="insertTD"/></tr>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="Cell" mode="insert">
		<xsl:variable name="offset"><xsl:apply-templates select="." mode="calc"/></xsl:variable>
		<xsl:if test="number($offset) mod ../@aid:tcols != 0">
			<xsl:call-template name="insertTD"/>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="insertTD">
		<td>
			<xsl:if test="@aid:ccols&gt;1">
				<xsl:attribute name="colspan"><xsl:value-of select="@aid:ccols"/></xsl:attribute>
			</xsl:if>
			<xsl:if test="@aid:crows&gt;1">
				<xsl:attribute name="rowspan"><xsl:value-of select="@aid:crows"/></xsl:attribute>
			</xsl:if>
		<xsl:apply-templates/></td>
		<xsl:apply-templates select="(following-sibling::Cell)[1]" mode="insert"/>
	</xsl:template>
	
	<xsl:template match="Tabell">
		<table><xsl:apply-templates select="Cell"/></table>
	</xsl:template>

	<xsl:template match="text()">
		<xsl:value-of select="translate(string(.), '&#x2028;&#x2029;', '')"/>
	</xsl:template>

<!--
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
	-->
	
		<!--
	<xsl:param name="debug" select="'false'"/>
	
	<xsl:variable name="min"><xsl:apply-templates select="(//page)[1]" mode="calcMin">
		<xsl:with-param name="cMin" select="number((//page)[1]/@no)"/>
	</xsl:apply-templates></xsl:variable>
	
	<xsl:variable name="max"><xsl:apply-templates select="(//page)[1]" mode="calcMax">
		<xsl:with-param name="cMax" select="number((//page)[1]/@no)"/>
	</xsl:apply-templates></xsl:variable>
	-->
	<!--
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
	</xsl:template>-->
	
	<!--
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
	</xsl:template>-->
	
	<!--
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
	</xsl:template>-->

</xsl:stylesheet>
