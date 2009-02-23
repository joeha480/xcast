<?xml version="1.0" encoding="utf-8"?>
<!--
*
* MIFXML to DTBook-NSFP
* Version 2007-03-28
* (Baserad på MIFXML to XHTML version 2005-03-31 14:36)
* Joel Håkansson, TPB
*
* Bilder hanteras endast delvis 
* Ekvationer hanteras inte
* Tabellhuvud och tabellfot kommer med, men saknar särskild uppmärkning
* Cellspan/Rowspan hanteras inte
* PgfNumStr kommer inte alltid med (p.g.a fulfix)
* Noter: Texten kommer på plats, direkt efter referensen. Numreringen fungerar inte på förankrade objekt.
* (Markers saknas)
* Wrappade P-taggar, är inget problem vid export till Word, men kan innebära problem vid konvertering till DTBook
* Vissa p-taggar har style-attribut (Ej tillåtet i XHTML)
* 
* Ändringar sedan förra versionen:
* Mellanslag är ok i filnamn
* 
* Ändringar i version 2005-04-27 16:01
* p1 -> p
* länkar till externa bildfiler
* tagit bort [] kring noter, sidnummer med i id:t.
* 
* Ändringar i version 2005-05-09 17:22
* pagenum har attribut page="normal" istället för class="pagenum-normal"
*
* Ändringar i version 2005-05-11 14:59
* Hämtar numreringen av fotnoter från Document-taggen. 
*        Problem: räknar noterna linjärt igenom dokumentet, vilket betyder att numreringen blir fel 
*                 om det förekommer noter i förankrade ramar.
*
* Ändringar i version 2005-06-16 13:01
* Exkluderar " ur elementnamn
* 
* Ändringar i version 2005-07-08 11:30
* Tillägg: Metadata i head
*
* Ändringar i version 2005-07-12 11:41
* id och idref på noter är med större sannolikhet unika
* bildlänkar har ej längre prefix bilder/
*
* Ändringar i version 2005-07-14 11:50
* TextLine hanteras i AFrame
*
* Ändringar i version 2005-10-03 11:57
* Tar bort ':' i elementnamn!
*
* Ändringar i version 2006-06-14 16:35
* Uppdatering av placeholders för metadata
*
* Ändringar i version 2007-03-05
* prefix för pagenum ändrat till "page-[nr]"
* En- och Em-hyphen konverteras nu ut som 0x2013 och 0x2014 (förut " - ")
*
* Ändringar i version 2007-03-28
* Dagger och double dagger konverteras nu ut som 0x2020 och 0x2021 (förut "")
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" indent="no" encoding="UTF-8"/>
  <xsl:key name="taggedParas" match="/MIFXML/TextFlow/Para" use="ParaLine/TextRectID/@attr1"/>
  <xsl:key name="tables" match="/MIFXML/Tbls/Tbl" use="TblID/@attr1"/>
  <xsl:key name="frames" match="/MIFXML/AFrames/Frame" use="ID/@attr1"/>
  
  <xsl:template name="replace">
	  <xsl:param name="node"/>
	  <xsl:variable name="result" select="translate(translate($node, ' .&quot;&lt;&gt;+=/,:', ''), 'åäöÅÄÖ', 'aaoAAO')"/>
	  <!-- Suck! -->
	  <xsl:if test="
				  starts-with($result,'0') or
				  starts-with($result,'1') or
				  starts-with($result,'2') or
				  starts-with($result,'3') or
				  starts-with($result,'4') or
				  starts-with($result,'5') or
				  starts-with($result,'6') or
				  starts-with($result,'7') or
				  starts-with($result,'8') or
				  starts-with($result,'9')">nf</xsl:if>
	  <xsl:value-of select="$result"/>
  </xsl:template>
  
   <xsl:template match="/">
     <xsl:comment>
				MIFXML to DTBook-NSFP version 2007-03-05
     </xsl:comment>
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
				<xsl:apply-templates select="." mode="part" />
			</bodymatter>
         </book>
      </dtbook>
   </xsl:template>

   <xsl:template match="/" mode="part">
<!-- Avoids multiple <html><head>...-tags -->
      <xsl:choose>
<!-- Book or document? -->
         <xsl:when test="count(*/MIFFile)&gt;0">
<!-- Mif-file -->
            <xsl:call-template name="file" />
         </xsl:when>
         <xsl:when test="count(*/Book)&gt;0">
<!-- Mif-Book -->
            <xsl:call-template name="book" />
         </xsl:when>
         <xsl:otherwise>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>

   <xsl:template name="book">
      <xsl:for-each select="*/BookComponent">
      <!--
         <xsl:message>
            <xsl:value-of select="concat(./FileName/@attr1,'.mif.xml')" />
         </xsl:message>
		-->
      </xsl:for-each>
      <xsl:call-template name="debug">
         <xsl:with-param name="value">Start</xsl:with-param>
      </xsl:call-template>
      <xsl:for-each select="*/BookComponent">
         <xsl:call-template name="debug">
            <xsl:with-param name="value">
               <xsl:value-of select="concat(./FileName/@attr1,'.mif.xml')" />
            </xsl:with-param>
         </xsl:call-template>
         <!-- <div class="{./FileName/@attr1}"> -->
            <xsl:apply-templates select="document(concat(substring-after(./FileName/@attr1, '&lt;c&gt;'),'.mif.xml'),/)" mode="part" />
         <!-- </div> -->
      </xsl:for-each>
      <xsl:call-template name="debug">
         <xsl:with-param name="value">Slut</xsl:with-param>
      </xsl:call-template>
   </xsl:template>

<!-- ska tas bort så småningom -->
   <xsl:template name="debug">
   <xsl:param name="value" />
   <xsl:param name="pn" />
   <xsl:param name="TFID"/>
   <debug>
      <xsl:value-of select="$value" />
   </debug>
   </xsl:template>
   
   <xsl:template name="warning">
   <xsl:param name="value" />
   <xsl:param name="pn" />
   <xsl:param name="TFID"/>
   <warning>
      <xsl:value-of select="$value" />
   </warning>
   </xsl:template>

   <xsl:template name="file">
      <xsl:for-each select="MIFXML/Page">
         <xsl:if test="./PageType/@attr1='BodyPage'">
            <xsl:variable name="pn" select="./PageNum/@attr1" />
            <xsl:variable name="firstTRID" select="generate-id(./TextRect[1])"/>
            <xsl:for-each select="child::TextRect">
<!-- Den här choose-satsen gör inget just nu, men kan vara intressant senare, när man väljer flera barn förutom TextRect -->
               <xsl:choose>
                  <xsl:when test="self::TextRect">
                    <!-- Hantera sidor utan textinnehåll -->
                    <xsl:variable name="TFID" select="./ID/@attr1"/>
                    <xsl:if test="count(key('taggedParas', $TFID))=0">
                      <p>
                        <xsl:call-template name="FormatPageNum">
                          <xsl:with-param name="pn" select="$pn"/>
                        </xsl:call-template>
                      </p>
                    </xsl:if>
                    <xsl:apply-templates select="." mode="pre">
                      <xsl:with-param name="pn" select="$pn"/>
                      <xsl:with-param name="firstTRID" select="$firstTRID"/>
                    </xsl:apply-templates>
                  </xsl:when>
                  <xsl:otherwise />
               </xsl:choose>
               
            </xsl:for-each>
         </xsl:if>
      </xsl:for-each>
   </xsl:template>
  
  <xsl:template match="TextLine">
	  <xsl:value-of select="String/@attr1"/>
  </xsl:template>
  
  <xsl:template match="TextRect" mode="pre">
    <xsl:param name="pn"/>
    <xsl:param name="firstTRID"/>
    
    <xsl:variable name="TFID" select="./ID/@attr1"/>
    <xsl:variable name="currentTRID" select="generate-id(.)"/>
    <xsl:variable name="taggedParas" select="key('taggedParas', $TFID)"/>
    
    <!--/MIFXML/TextFlow/Para[ParaLine/TextRectID/@attr1=$TFID]"-->
    <xsl:variable name="firstID" select="generate-id(key('taggedParas', $TFID)[1])"/>
  
    <!-- För varje Para som har TextRectID=$TFID -->
    <xsl:for-each select="$taggedParas">                 
      <!-- Om det här är första stycket på en ny sida, så ge variablen pnOk rätt nummer -->
      <xsl:variable name="pnOk">
        <xsl:choose>
          <xsl:when test="generate-id(current())=$firstID and $currentTRID=$firstTRID"><xsl:value-of select="$pn"/></xsl:when>
          <xsl:otherwise/>
        </xsl:choose>
      </xsl:variable>
      <!-- Det blir problem när ett stycke innehåller flera textrectId med olika värden eftersom 
           kontrollen sker på styckenivå. Detta är inte så vanligt, men kan förekomma. Just nu markeras detta endast för
           manuell kontroll. -->
      <xsl:choose>
        <!-- Det finns bara ett ID eller alla IDn är lika. -->
        <xsl:when test="count(./ParaLine/TextRectID)=count(./ParaLine/TextRectID[@attr1=$TFID])">
          <xsl:apply-templates select=".">
            <xsl:with-param name="pn" select="$pnOk"/>
            <xsl:with-param name="TFID" select="$TFID"/>
          </xsl:apply-templates>
          <!-- Välj alla följande Para (som inte själva har ett eget TextRectID) 
               som har den här Para som (närmsta föregående granne med ett TextRectID) -->
          <xsl:apply-templates select="following-sibling::Para[ not(ParaLine/TextRectID) and 
                                (generate-id(preceding-sibling::Para[ParaLine/TextRectID][1])) = generate-id(current())]"/>
        </xsl:when>
        <!-- Det finns flera IDn och de är olika. -->
        <!-- Är detta det första stycket med detta id? -->
        <xsl:when test="generate-id(current())=generate-id($taggedParas[1])">
          <xsl:choose>
            <!-- Är detta det sista ID:t i stycket? -->

            <xsl:when test="generate-id(./ParaLine/TextRectID[last()])=generate-id(./ParaLine/TextRectID[@attr1=$TFID][last()])">
              <xsl:call-template name="warning">
                <xsl:with-param name="value">I följande stycke finns flera sidbrytningar, varav endast den första är utmärkt.</xsl:with-param>
              </xsl:call-template>
              <xsl:apply-templates select="."><xsl:with-param name="pn" select="$pnOk" /><xsl:with-param name="TFID" select="$TFID"/></xsl:apply-templates>
              <xsl:apply-templates select="following-sibling::Para[ not(ParaLine/TextRectID) and 
                              (generate-id(preceding-sibling::Para[ParaLine/TextRectID][1])) = generate-id(current())]"/>
            </xsl:when>
            <!-- Om inte, så behandla detta stycke särskilt -->
            <xsl:otherwise>

              <!-- Men efterföljande stycken som vanligt -->
              <!--<xsl:apply-templates select="following-sibling::Para[ not(ParaLine/TextRectID) and 
                              (generate-id(preceding-sibling::Para[ParaLine/TextRectID][1])) = generate-id(current())]"/>      -->
            </xsl:otherwise>
          </xsl:choose>
        </xsl:when>
        <!-- Det finns föregående stycken med detta id. Lämna obehandlat -->
        <xsl:otherwise>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="Para" mode="special">
  <xsl:param name="pn">0</xsl:param>
  <xsl:param name="TFID"/>
        <xsl:call-template name="warning">
        <xsl:with-param name="value">SPECIAL BEGIN ---</xsl:with-param>
     </xsl:call-template>
     <xsl:variable name="ename">
       <xsl:choose>
        <xsl:when test="string-length(PgfTag/@attr1)=0">p</xsl:when>
        <xsl:otherwise><!-- <xsl:value-of select="translate(PgfTag/@attr1, $replace, '')"/>-->
        <xsl:call-template name="replace"><xsl:with-param name="node" select="PgfTag/@attr1"/></xsl:call-template>
        </xsl:otherwise>
      </xsl:choose>
      </xsl:variable>
  <xsl:element name="{$ename}">
<!-- behandla noder före första fontnoden -->
<!--  <span style="background-color: #ff80ff"> -->
        <xsl:apply-templates select="ParaLine/*[count(../preceding-sibling::ParaLine/Font)+count(preceding-sibling::Font)=0]">
           <xsl:with-param name="pn" select="$pn" />
           <xsl:with-param name="TFID" select="$TFID"/>
        </xsl:apply-templates>
<!--  </span> -->
<!-- behandla alla fontnoder (och innehållet som följer fram till nästa fontnod)-->
     <xsl:apply-templates select="ParaLine/Font" mode="ft">
        <xsl:with-param name="pn" select="$pn" />
        <xsl:with-param name="TFID" select="$TFID"/>
     </xsl:apply-templates>
  </xsl:element>
        <xsl:call-template name="warning">
        <xsl:with-param name="value">--- SPECIAL END</xsl:with-param>
     </xsl:call-template>   
  </xsl:template>
  
<!-- Används troligtvis inte -->
   <xsl:template name="style">
   <xsl:param name="pn" />
   <xsl:param name="TFID"/>
   <xsl:attribute name="style">background-color: #c0c0c0; border-style: solid; border-width: 1px; border-color: #ff0000; margin: 1px;</xsl:attribute>
   </xsl:template>

<!-- välj alla barn till den här nodens förälders följande ParaLine-syskon som har den här fontnoden som sin närmaste föregående förälders syskonbarn-->
<!-- generate-id(parent::preceding-sibling::ParaLine::Font[last()]) = generate-id(current()) -->
<!-- parent::following-sibling::ParaLine::child::following-sibling[1] -->
   <xsl:template match="Font" mode="ft">
   <xsl:param name="pn" />
   <xsl:param name="TFID"/>
   <xsl:variable name="Current" select="." />
<!-- Om teckenformatet finns i katalogen så välj den noden istället, annars välj aktuell nod -->
<!-- Hanterar med andra ord inte överlagring av namngivna teckenformat -->
   <xsl:variable name="FontInfo" select="//FontCatalog/Font[FTag/@attr1=$Current/FTag/@attr1]|$Current[not(string(FTag/@attr1))]" />
   <xsl:choose>
<!-- Sparar teckenformatsnamnet i ett klass-attribut men översätter ändå vanliga teckenformat -->
      <xsl:when test="string($FontInfo/FTag/@attr1)">
      <xsl:variable name="ename">
       <xsl:choose>
        <xsl:when test="string-length($FontInfo/FTag/@attr1)=0">span</xsl:when>
        <xsl:otherwise><!--<xsl:value-of select="translate($FontInfo/FTag/@attr1, $replace, '')"/>-->
        <xsl:call-template name="replace"><xsl:with-param name="node" select="$FontInfo/FTag/@attr1"/></xsl:call-template>
        </xsl:otherwise>
      </xsl:choose>
      </xsl:variable>
         <xsl:element name="{$ename}">
         <xsl:attribute name="class">span</xsl:attribute>
            <xsl:call-template name="FontTests-Start">
               <xsl:with-param name="Current" select="$FontInfo" />
               <xsl:with-param name="pn" select="$pn" />
               <xsl:with-param name="TFID" select="$TFID"/>
            </xsl:call-template>
         </xsl:element>
      </xsl:when>
      <xsl:otherwise>
         <xsl:call-template name="FontTests-Start">
            <xsl:with-param name="Current" select="$FontInfo" />
            <xsl:with-param name="pn" select="$pn" />
            <xsl:with-param name="TFID" select="$TFID"/>
         </xsl:call-template>
      </xsl:otherwise>
   </xsl:choose>
   </xsl:template>

   <xsl:template name="FontTests-Final">
   <xsl:param name="pn" />
   <xsl:param name="TFID"/>
   <xsl:call-template name="getTextNodes">
      <xsl:with-param name="pn" select="$pn" />
      <xsl:with-param name="TFID" select="$TFID"/>
   </xsl:call-template>
   </xsl:template>

   <xsl:template name="FontTests-03">
   <xsl:param name="Current" />
   <xsl:param name="pn" />
   <xsl:param name="TFID"/>
   <xsl:choose>
      <xsl:when test="$Current/FWeight/@attr1='Bold' or $Current/FWeight/@attr1='SemiBold'">
         <strong>
            <xsl:call-template name="FontTests-Final">
               <xsl:with-param name="Current" select="$Current" />
               <xsl:with-param name="TFID" select="$TFID"/>
               <xsl:with-param name="pn" select="$pn" />
            </xsl:call-template>
         </strong>
      </xsl:when>
      <xsl:otherwise>
         <xsl:call-template name="FontTests-Final">
            <xsl:with-param name="Current" select="$Current" />
            <xsl:with-param name="pn" select="$pn" />
            <xsl:with-param name="TFID" select="$TFID"/>
         </xsl:call-template>
      </xsl:otherwise>
   </xsl:choose>
   </xsl:template>

   <xsl:template name="FontTests-02">
   <xsl:param name="Current" />
   <xsl:param name="pn" />
   <xsl:param name="TFID"/>
   <xsl:choose>
      <xsl:when test="$Current/FPosition/@attr1='FSubscript'">
         <sub>
            <xsl:call-template name="FontTests-03">
               <xsl:with-param name="Current" select="$Current" />
               <xsl:with-param name="pn" select="$pn" />
               <xsl:with-param name="TFID" select="$TFID"/>
            </xsl:call-template>
         </sub>
      </xsl:when>

      <xsl:otherwise>
         <xsl:call-template name="FontTests-03">
            <xsl:with-param name="Current" select="$Current" />
            <xsl:with-param name="pn" select="$pn" />
            <xsl:with-param name="TFID" select="$TFID"/>
         </xsl:call-template>
      </xsl:otherwise>
   </xsl:choose>
   </xsl:template>

   <xsl:template name="FontTests-01">
   <xsl:param name="Current" />
   <xsl:param name="pn" />
   <xsl:param name="TFID"/>
   <xsl:choose>
      <xsl:when test="$Current/FPosition/@attr1='FSuperscript'">
         <sup>
            <xsl:call-template name="FontTests-02">
               <xsl:with-param name="Current" select="$Current" />
               <xsl:with-param name="TFID" select="$TFID"/>

               <xsl:with-param name="pn" select="$pn" />
            </xsl:call-template>
         </sup>
      </xsl:when>
      <xsl:otherwise>
         <xsl:call-template name="FontTests-02">
            <xsl:with-param name="Current" select="$Current" />
            <xsl:with-param name="pn" select="$pn" />
            <xsl:with-param name="TFID" select="$TFID"/>
         </xsl:call-template>
      </xsl:otherwise>
   </xsl:choose>
   </xsl:template>

   <xsl:template name="FontTests-Start">
   <xsl:param name="Current" />
   <xsl:param name="pn" />
   <xsl:param name="TFID"/>
   <xsl:choose>
      <xsl:when test="$Current/FAngle/@attr1='Italic'">
         <em>
            <xsl:call-template name="FontTests-01">
               <xsl:with-param name="Current" select="$Current" />
               <xsl:with-param name="pn" select="$pn" />
               <xsl:with-param name="TFID" select="$TFID"/>
            </xsl:call-template>
         </em>
      </xsl:when>
      <xsl:otherwise>
         <xsl:call-template name="FontTests-01">
            <xsl:with-param name="Current" select="$Current" />
            <xsl:with-param name="pn" select="$pn" />
            <xsl:with-param name="TFID" select="$TFID"/>
         </xsl:call-template>
      </xsl:otherwise>
   </xsl:choose>
   </xsl:template>

   <xsl:template name="getTextNodes">
   <xsl:param name="pn" />
   <xsl:param name="TFID"/>
<!-- välj alla följande syskon som har den här fontnoden som närmaste föregående syskon -->
   <xsl:apply-templates select="following-sibling::*[generate-id(preceding-sibling::Font[1]) = generate-id(current())]">
      <xsl:with-param name="pn" select="$pn" />
      <xsl:with-param name="TFID" select="$TFID"/>
   </xsl:apply-templates>
<!-- Dont ask, it works ;) -->
   <xsl:apply-templates select=" ../following-sibling::ParaLine/*[generate-id(((../preceding-sibling::ParaLine/Font)|(preceding-sibling::Font))[last()]) = generate-id(current()) ]">
      <xsl:with-param name="pn" select="$pn" />
      <xsl:with-param name="TFID" select="$TFID"/>
   </xsl:apply-templates>
   </xsl:template>

   <xsl:template match="Para">
   <xsl:param name="pn">0</xsl:param>
   <xsl:param name="TFID"/>
   <xsl:variable name="ename">
       <xsl:choose>
        <xsl:when test="string-length(PgfTag/@attr1)=0">p</xsl:when>
        <xsl:otherwise><!-- <xsl:value-of select="translate(PgfTag/@attr1, $replace, '')"/> -->
        <xsl:call-template name="replace"><xsl:with-param name="node" select="PgfTag/@attr1"/></xsl:call-template>
        </xsl:otherwise>
      </xsl:choose>
      </xsl:variable>
   <xsl:element name="{$ename}">
<!-- behandla noder före första fontnoden -->
<!--  <span style="background-color: #ff80ff"> -->
         <xsl:apply-templates select="ParaLine/*[count(../preceding-sibling::ParaLine/Font)+count(preceding-sibling::Font)=0]">
            <xsl:with-param name="pn" select="$pn" />
            <xsl:with-param name="TFID" select="$TFID"/>
         </xsl:apply-templates>
<!--  </span> -->
<!-- behandla alla fontnoder (och innehållet som följer fram till nästa fontnod)-->
      <xsl:apply-templates select="ParaLine/Font" mode="ft">
         <xsl:with-param name="pn" select="$pn" />
         <xsl:with-param name="TFID" select="$TFID"/>
      </xsl:apply-templates>
   </xsl:element>
   </xsl:template>

   <xsl:template name="FormatPageNum">
   <xsl:param name="pn">0</xsl:param>
   <xsl:param name="TFID"/>
   <xsl:if test="$pn &gt; 0">
      <pagenum id="page-{$pn}" page="normal">
         <xsl:value-of select="$pn" />
      </pagenum>
   </xsl:if>
   </xsl:template>
   
   <xsl:template match="TextRectID">
   <xsl:param name="pn" />
   <xsl:param name="TFID"/>
   <xsl:if test="$TFID=@attr1">
   <xsl:call-template name="FormatPageNum">
     <xsl:with-param name="pn" select="$pn"/>
     <xsl:with-param name="TFID" select="$TFID"/>
   </xsl:call-template>
   </xsl:if>
   </xsl:template>

   <xsl:template match="Row" mode="table">
         <xsl:for-each select=".">
         <tr>
            <xsl:for-each select="./Cell">
               <td>
                  <xsl:apply-templates select="CellContent/Para">
                     <!-- <xsl:with-param name="pn" select="$pn" /> -->
                  </xsl:apply-templates>
               </td>
            </xsl:for-each>
         </tr>
      </xsl:for-each>
   </xsl:template>
   
   <xsl:template match="ATbl">
   <!-- <xsl:param name="pn" /> -->
   <xsl:variable name="id" select="@attr1" />
   <xsl:apply-templates select="key('tables', $id)/TblTitle/TblTitleContent/Para"/>
   <table border="1">
      <xsl:apply-templates select="key('tables', $id)/TblH/Row" mode="table"/>
      <xsl:apply-templates select="key('tables', $id)/TblBody/Row" mode="table"/>
      <xsl:apply-templates select="key('tables', $id)/TblF/Row" mode="table"/>
   </table>
   </xsl:template>

   <xsl:template match="AFrame">
   <!-- <xsl:param name="pn" /> -->
   <xsl:variable name="id" select="@attr1" />
   <xsl:for-each select="key('frames', $id)/*">
      <xsl:choose>
         <xsl:when test="self::TextRect">
         <!--
            <xsl:variable name="id2" select="./ID/@attr1" />
            <xsl:variable name="fp2" select="/MIFXML/TextFlow/Para[ParaLine/TextRectID/@attr1=$id2]" />
            <xsl:apply-templates select="$fp2[1]">
               <xsl:with-param name="TFID" select="$id2" />-->
               <!-- <xsl:with-param name="pn" select="$pn" /> -->
               <!-- <xsl:with-param name="TRNext" select="0" />
            </xsl:apply-templates> -->
            <!-- ful-lösning för att fixa paras i AFrame, kolla upp -->
            <!-- <xsl:for-each select="$fp2[1]">
            <xsl:apply-templates select="following-sibling::Para[ not(ParaLine/TextRectID) and 
                               (generate-id(preceding-sibling::Para[ParaLine/TextRectID][1])) = generate-id(current())]"/>
            </xsl:for-each>-->
            <!-- Ska ersätta det som står ovanför genom modularisering -->
            <xsl:apply-templates select="." mode="pre"/>
            
         </xsl:when>
         <xsl:when test="self::TextLine">
			 <xsl:apply-templates select="."/>
         </xsl:when>
         <xsl:when test="self::ImportObject">
            <xsl:choose>
              <xsl:when test="ImportObFileDI/@attr1!=''">
                <img src="" alt="">
                   <xsl:attribute name="src"><!--bilder/--><xsl:call-template name="getDIFileName">
                       <xsl:with-param name="value" select="ImportObFileDI/@attr1"/>
                   </xsl:call-template>.jpg</xsl:attribute>
                </img>
              </xsl:when>
              <xsl:otherwise>
                <EmbeddedObjectNotIncluded class="span">(OBJECT:Embedded)</EmbeddedObjectNotIncluded>
              </xsl:otherwise>
            </xsl:choose>
         </xsl:when>
         <xsl:when test="self::Math">
            <MathObjectNotIncluded class="span">(OBJECT:Math)</MathObjectNotIncluded>
         </xsl:when>
         <xsl:otherwise />
      </xsl:choose>
   </xsl:for-each>
   </xsl:template>
   
<xsl:template name="getDIFileName">
  <xsl:param name="value"/>
  <xsl:choose>
    <xsl:when test="string-length(substring-after($value, '&lt;c&gt;'))=0"><xsl:value-of select="$value"/></xsl:when>
    <xsl:otherwise>
       <xsl:call-template name="getDIFileName">
         <xsl:with-param name="value" select="substring-after($value, '&lt;c&gt;')"/>
      </xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

   <xsl:template match="*">
   <xsl:param name="pn" />
   <xsl:param name="TFID"/>
   <xsl:value-of select="." />
   </xsl:template>

   <xsl:template match="String">
   <xsl:param name="pn" />
   <xsl:param name="TFID"/>
   <xsl:if test="@attr1 = ../../ParaLine[1]/String[1]/@attr1">
      <xsl:value-of select="../../PgfNumString/@attr1" />
   </xsl:if>
   <xsl:value-of select="@attr1" />
   </xsl:template>
   
   <xsl:template match="FNote">
     <xsl:param name="pn">0</xsl:param>
     <xsl:variable name="refid" select="@attr1"/>
     <xsl:variable name="number" select="count(preceding::FNote[not(parent::Notes)])"/>
     <xsl:variable name="id" select="../../../Notes/FNote[ID/@attr1=$refid]/Para/Unique/@attr1"/>
     <xsl:variable name="fnotestartnum">
       <xsl:choose>
         <xsl:when test="number(/MIFXML/Document/FNoteStartNum/@attr1)">
           <xsl:value-of select="/MIFXML/Document/FNoteStartNum/@attr1"/>
         </xsl:when>
         <xsl:otherwise>1</xsl:otherwise>
       </xsl:choose>
     </xsl:variable>
     <noteref idref="fn_{$pn}_{$number}-{@attr1}-{$id}"><xsl:value-of select="$number+$fnotestartnum"/></noteref>
     <note id="fn_{$pn}_{$number}-{@attr1}-{$id}">
       <xsl:value-of select="$number+$fnotestartnum"/>. <xsl:apply-templates select="../../../Notes/FNote[ID/@attr1=$refid]/Para/ParaLine/*"/>
     </note>
   </xsl:template>

   <xsl:template match="Char">
   <xsl:param name="pn" />
   <xsl:param name="TFID"/>
<!--
Tab, HardSpace, SoftHyphen, HardHyphen, DiscHyphen, NoHyphen, Cent, Pound, Yen, EnDash,
EmDash, Dagger, DoubleDagger, Bullet, HardReturn, NumberSpace, ThinSpace, EnSpace, EmSpace.
* Mif Reference p. 123 *
-->
   <xsl:choose>
     <xsl:when test="@attr1='Tab'"><xsl:value-of select="'&#x0009;'"/></xsl:when>
     <xsl:when test="@attr1='HardSpace'"><xsl:value-of select="' '"/></xsl:when>
     <xsl:when test="@attr1='SoftHyphen'"/>
     <xsl:when test="@attr1='HardHyphen'">-</xsl:when>
     <xsl:when test="@attr1='DiscHyphen'"/>
     <xsl:when test="@attr1='NoHyphen'"/>
     <xsl:when test="@attr1='Cent'"><xsl:value-of select="'&#x00A2;'"/></xsl:when>
     <xsl:when test="@attr1='Pound'"><xsl:value-of select="'&#x00A3;'"/></xsl:when>
     <xsl:when test="@attr1='Yen'"><xsl:value-of select="'&#x00A5;'"/></xsl:when>
     <xsl:when test="@attr1='EnDash'"><xsl:value-of select="'&#x2013;'"/></xsl:when>
     <xsl:when test="@attr1='EmDash'"><xsl:value-of select="'&#x2014;'"/></xsl:when>
     <xsl:when test="@attr1='Dagger'"><xsl:value-of select="'&#x2020;'"/></xsl:when>
     <xsl:when test="@attr1='DoubleDagger'"><xsl:value-of select="'&#x2021;'"/></xsl:when>
     <xsl:when test="@attr1='Bullet'"><xsl:value-of select="'&#x2022;'"/></xsl:when>
     <xsl:when test="@attr1='HardReturn'"><br></br></xsl:when>
     <xsl:when test="@attr1='NumberSpace' or @attr1='ThinSpace' or @attr1='EnSpace' or @attr1='EmSpace'">
        <xsl:value-of select="' '"/>
      </xsl:when>
     <xsl:otherwise>
       <xsl:call-template name="warning">
         <xsl:with-param name="value" select="concat('Oförutsett element: ', @attr1)"/>
       </xsl:call-template>
     </xsl:otherwise>
   </xsl:choose>
   </xsl:template>
</xsl:stylesheet>

