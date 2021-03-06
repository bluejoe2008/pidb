package cn.pidb.engine.util

import java.io.{File, FileInputStream, InputStream}

import cn.pidb.blob._
import cn.pidb.util.StreamUtils._
import org.apache.commons.codec.digest.DigestUtils
import org.apache.hadoop.hbase.client.{Delete, Get, Put}
import org.apache.hadoop.hbase.util.Bytes

object HBaseUtils {
  //column name: using uppercase
  val columnFamily : Array[Byte] = Bytes.toBytes("blob")
  val qualifyFamilyBlob : Array[Byte] = Bytes.toBytes("blobValue")
  val qualifyFamilyMT : Array[Byte] = Bytes.toBytes("mimeType")
  val qualifyFamilyLen : Array[Byte] = Bytes.toBytes("length")

  //TODO: maybe not effective
  val md5 = DigestUtils.getMd5Digest
  def makeRowKey(blobId: BlobId): Array[Byte] ={
    blobId.asByteArray().reverse
    //md5.digest(blobId.asByteArray())
  }

  def buildPut(blob: Blob, blobId: BlobId) : Put = {
    val retPut : Put = new Put(makeRowKey(blobId))
    retPut.addColumn(columnFamily, qualifyFamilyBlob, blob.toBytes())
    retPut.addColumn(columnFamily, qualifyFamilyMT, Bytes.toBytes(blob.mimeType.code)) //mime
    retPut.addColumn(columnFamily, qualifyFamilyLen, Bytes.toBytes(blob.length)) //length
  }

  def buildPut(blobFile: File, blobIdFac : BlobIdFactory) : Put = {
    val fis = new FileInputStream(blobFile)
    val blobId = blobIdFac.readFromStream(fis)
    val mimeType = MimeType.fromCode(fis.readLong())
    val length = fis.readLong()
    fis.close()

    val blob = Blob.fromInputStreamSource(new InputStreamSource() {
      def offerStream[T](consume: InputStream => T): T = {
        val is = new FileInputStream(blobFile)
        //NOTE: skip
        is.skip(8 * 4)
        val t = consume(is)
        is.close()
        t
      }
    }, length, Some(mimeType))

    buildPut(blob, blobId)
  }

  def buildDelete(blobId: BlobId) : Delete = {
    val delete : Delete = new Delete(makeRowKey(blobId))
    delete.addColumns(columnFamily, qualifyFamilyBlob)
    delete.addColumns(columnFamily, qualifyFamilyMT)
    delete.addColumns(columnFamily, qualifyFamilyLen)
  }

  def buildGetBlob(blobId: BlobId) : Get = {
    new Get(makeRowKey(blobId))
  }
}
